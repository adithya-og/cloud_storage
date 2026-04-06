package com.dev.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blockchain_blocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainBlock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "block_index", nullable = false)
    private Long blockIndex;
    
    @Column(name = "previous_hash", nullable = false)
    private String previousHash;
    
    @Column(nullable = false)
    private String hash;
    
    @Lob
    @Column(name = "data", nullable = false)
    private String data; // JSON string containing file operation details
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private Integer nonce = 0;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}