package com.kbase.model;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "files")
public class Files {
    @Id
    @Column(name = "file_id")
    private int id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;
    
    @Column(name = "author")
    private String author;

    @Column(name = "make_date")
    private Date makeDate;

    @Column(name = "update_date")
    private Date updateDate;
    
    @Column(name = "file_path")
    private String filePath;
    
    
    public Files(int id, String author, String fileName, String filePath, String fileType, Date makeDate, Date updateDate) {
        this.id = id;
        this.author = author;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.makeDate = makeDate;
        this.updateDate = updateDate;
    }

	public Files() {
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getFileId() {
		return id;
	}

	public void setFileId(int fileId) {
		this.id = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getMakeDate() {
		return makeDate;
	}

	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	
}
