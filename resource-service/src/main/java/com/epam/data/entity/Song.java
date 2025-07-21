package com.epam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(of = "data")
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(
        name = "songs",
        uniqueConstraints = @UniqueConstraint(columnNames = "checksum"),
        indexes = {@Index(columnList = "id"), @Index(columnList = "checksum")}
)
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(name = "data")
    private byte[] data;

    @Column(name = "checksum", updatable = false, nullable = false, unique = true)
    private String checksum;
}
