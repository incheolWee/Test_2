package com.example.test_2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "file_test")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = " ID")
    private long id;

    @Column(name ="file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name= "file_writer")
    private String fileWriter;

    @Column(name="file_size")
    private String fileSize;

    @Column(name="create_date")
    private String creteData;

    @Column(name="update_data")
    private String updateData;

}
