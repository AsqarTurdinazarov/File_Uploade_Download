package com.example.file_uploade_download.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class FileFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String faylOrginalName;
    @Column(nullable = false)
    private long faylHajmi;
    @Column(nullable = false)
    private String faylTuri;
    @Column(nullable = false,unique = true)
    private String faylYangiNomi;
}
