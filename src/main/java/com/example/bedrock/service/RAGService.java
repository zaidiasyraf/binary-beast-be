package com.example.bedrock.service;

import com.example.bedrock.dto.ChatRequest;
import com.example.bedrock.dto.ChatResponse;
import com.example.bedrock.entity.*;
import com.example.bedrock.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RAGService {

    private static final Logger logger = LoggerFactory.getLogger(RAGService.class);

    private final EmbeddingService embeddingService;
    private final BedrockService bedrockService;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRelationshipRepository customerRelationshipRepository;
    private final int maxRetrievalResults;

    public RAGService(EmbeddingService embeddingService,
                     BedrockService bedrockService,
                     CustomerRepository customerRepository,
                     AccountRepository accountRepository,
                     TransactionRepository transactionRepository,
                     CustomerRelationshipRepository customerRelationshipRepository,
                     @Value("${rag.max-retrieval-results:5}") int maxRetrievalResults) {
        this.embeddingService = embeddingService;
        this.bedrockService = bedrockService;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerRelationshipRepository = customerRelationshipRepository;
        this.maxRetrievalResults = maxRetrievalResults;
    }

    /**
     * Process a chat request with RAG - retrieve relevant context and enhance the prompt
     */
    public ChatResponse chatWithRAG(ChatRequest request) {
        try {
            // Generate embedding for the user query
            logger.info("Generating embedding for query: {}", request.getMessage());
            List<Float> queryEmbedding = embeddingService.generateEmbedding(request.getMessage());
            String queryVectorString = embeddingService.embeddingToVectorString(queryEmbedding);
            logger.info("Generated embedding vector (length: {})", queryEmbedding.size());

            // Retrieve relevant context from database
            logger.info("Retrieving relevant context from database...");
            List<String> contextItems = retrieveRelevantContext(queryVectorString);
            logger.info("Retrieved {} context items", contextItems.size());
            
            if (contextItems.isEmpty()) {
                logger.warn("No context items retrieved. This might mean embeddings haven't been generated yet.");
                logger.warn("Please run: POST /api/bedrock/rag/generate-embeddings");
            }

            // Build enhanced prompt with context
            String enhancedMessage = buildEnhancedPrompt(request.getMessage(), contextItems);
            logger.debug("Enhanced prompt length: {}", enhancedMessage.length());

            // Create new request with enhanced message
            ChatRequest enhancedRequest = new ChatRequest();
            enhancedRequest.setMessage(enhancedMessage);
            enhancedRequest.setConversationHistory(request.getConversationHistory());

            // Call Bedrock service with enhanced prompt
            return bedrockService.chat(enhancedRequest);
            
        } catch (Exception e) {
            logger.error("Error in RAG processing: {}", e.getMessage(), e);
            // Fallback to regular chat if RAG fails
            logger.warn("Falling back to regular chat without RAG");
            return bedrockService.chat(request);
        }
    }

    /**
     * Retrieve relevant context from database using vector similarity search
     */
    private List<String> retrieveRelevantContext(String queryVectorString) {
        List<String> contextItems = new ArrayList<>();

        // Search customers
        try {
            logger.debug("Searching for similar customers...");
            List<Customer> similarCustomers = customerRepository.findSimilarCustomers(queryVectorString, maxRetrievalResults);
            logger.debug("Found {} similar customers", similarCustomers.size());
            for (Customer customer : similarCustomers) {
                if (customer.getContentText() != null && !customer.getContentText().isEmpty()) {
                    contextItems.add("Customer: " + customer.getContentText());
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving customers: {}", e.getMessage(), e);
        }

        // Search accounts
        try {
            logger.debug("Searching for similar accounts...");
            List<Account> similarAccounts = accountRepository.findSimilarAccounts(queryVectorString, maxRetrievalResults);
            logger.debug("Found {} similar accounts", similarAccounts.size());
            for (Account account : similarAccounts) {
                if (account.getContentText() != null && !account.getContentText().isEmpty()) {
                    contextItems.add("Account: " + account.getContentText());
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving accounts: {}", e.getMessage(), e);
        }

        // Search transactions
        try {
            logger.debug("Searching for similar transactions...");
            List<Transaction> similarTransactions = transactionRepository.findSimilarTransactions(queryVectorString, maxRetrievalResults);
            logger.debug("Found {} similar transactions", similarTransactions.size());
            for (Transaction transaction : similarTransactions) {
                if (transaction.getContentText() != null && !transaction.getContentText().isEmpty()) {
                    contextItems.add("Transaction: " + transaction.getContentText());
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving transactions: {}", e.getMessage(), e);
        }

        // Search customer relationships
        try {
            logger.debug("Searching for similar relationships...");
            List<CustomerRelationship> similarRelationships = customerRelationshipRepository.findSimilarRelationships(queryVectorString, maxRetrievalResults);
            logger.debug("Found {} similar relationships", similarRelationships.size());
            for (CustomerRelationship relationship : similarRelationships) {
                if (relationship.getContentText() != null && !relationship.getContentText().isEmpty()) {
                    contextItems.add("Relationship: " + relationship.getContentText());
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving relationships: {}", e.getMessage(), e);
        }

        return contextItems;
    }

    /**
     * Build enhanced prompt with retrieved context
     */
    private String buildEnhancedPrompt(String userQuery, List<String> contextItems) {
        if (contextItems.isEmpty()) {
            return userQuery;
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a helpful assistant for Aeon Bank Malaysia. Use the following context information to answer the user's question accurately.\n\n");
        prompt.append("Context Information:\n");
        for (int i = 0; i < contextItems.size(); i++) {
            prompt.append((i + 1)).append(". ").append(contextItems.get(i)).append("\n");
        }
        prompt.append("\nUser Question: ").append(userQuery);
        prompt.append("\n\nPlease provide a helpful answer based on the context above. If the context doesn't contain relevant information, say so.");

        return prompt.toString();
    }

    /**
     * Generate and store embeddings for existing records (for initial setup)
     * This can be called to populate embeddings for existing data
     */
    public void generateEmbeddingsForExistingData() {
        // Generate embeddings for customers
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            if (customer.getContentText() != null && customer.getEmbedding() == null) {
                try {
                    List<Float> embedding = embeddingService.generateEmbedding(customer.getContentText());
                    String embeddingString = embeddingService.embeddingToVectorString(embedding);
                    customerRepository.updateCustomerEmbedding(customer.getCustomerId(), embeddingString);
                    logger.info("Generated embedding for customer {}", customer.getCustomerId());
                } catch (Exception e) {
                    logger.error("Error generating embedding for customer {}: {}", customer.getCustomerId(), e.getMessage(), e);
                }
            }
        }

        // Generate embeddings for accounts
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            if (account.getContentText() != null && account.getEmbedding() == null) {
                try {
                    List<Float> embedding = embeddingService.generateEmbedding(account.getContentText());
                    String embeddingString = embeddingService.embeddingToVectorString(embedding);
                    accountRepository.updateAccountEmbedding(account.getAccountId(), embeddingString);
                    logger.info("Generated embedding for account {}", account.getAccountId());
                } catch (Exception e) {
                    logger.error("Error generating embedding for account {}: {}", account.getAccountId(), e.getMessage(), e);
                }
            }
        }

        // Generate embeddings for transactions
        List<Transaction> transactions = transactionRepository.findAll();
        for (Transaction transaction : transactions) {
            if (transaction.getContentText() != null && transaction.getEmbedding() == null) {
                try {
                    List<Float> embedding = embeddingService.generateEmbedding(transaction.getContentText());
                    String embeddingString = embeddingService.embeddingToVectorString(embedding);
                    transactionRepository.updateTransactionEmbedding(transaction.getTransactionId(), embeddingString);
                    logger.info("Generated embedding for transaction {}", transaction.getTransactionId());
                } catch (Exception e) {
                    logger.error("Error generating embedding for transaction {}: {}", transaction.getTransactionId(), e.getMessage(), e);
                }
            }
        }

        // Generate embeddings for relationships
        List<CustomerRelationship> relationships = customerRelationshipRepository.findAll();
        for (CustomerRelationship relationship : relationships) {
            if (relationship.getContentText() != null && relationship.getEmbedding() == null) {
                try {
                    List<Float> embedding = embeddingService.generateEmbedding(relationship.getContentText());
                    String embeddingString = embeddingService.embeddingToVectorString(embedding);
                    customerRelationshipRepository.updateRelationshipEmbedding(relationship.getRelationshipId(), embeddingString);
                    logger.info("Generated embedding for relationship {}", relationship.getRelationshipId());
                } catch (Exception e) {
                    logger.error("Error generating embedding for relationship {}: {}", relationship.getRelationshipId(), e.getMessage(), e);
                }
            }
        }
    }
}

