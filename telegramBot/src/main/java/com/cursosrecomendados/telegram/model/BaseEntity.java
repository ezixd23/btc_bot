package com.cursosrecomendados.telegram.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.common.base.Objects;

@MappedSuperclass()
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "created_at")
	private Date createdAt;	
	
	@Column(name = "updated_at")
	private Date updatedAt;	
	
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
    @Override
    public String toString() {
        return String.format("%s(id=%d, created_at=%s, updated_at=%s)", 
        		this.getClass().getSimpleName(), 
        		this.getId(),
        		this.getCreatedAt(),
        		this.getUpdatedAt());
    }
    

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof BaseEntity) {
            final BaseEntity other = (BaseEntity) o;
            return Objects.equal(getId(), other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
