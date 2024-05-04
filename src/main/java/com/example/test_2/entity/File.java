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
    private String file_name;

    @Column(name = "file_path")
    private String file_path;

    @Column(name= "file_writer")
    private String file_writer;

    @Column(name="file_size")
    private String file_size;

    @Column(name="create_date")
    private String crete_data;

    @Column(name="update_data")
    private String update_data;

}
