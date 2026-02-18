package com.codeit.monew.interest.entity;

import com.codeit.monew.common.entity.AbstractEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "interest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest extends AbstractEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ElementCollection
    @CollectionTable(name = "interest_keyword", joinColumns = @JoinColumn(name = "interest_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();

    private long subscriberCount;

    @Builder
    public Interest(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords != null ? keywords : new ArrayList<>();
        this.subscriberCount = 0;
    }

    public void updateKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void increaseSubscriberCount() {
        this.subscriberCount++;
    }

    public void decreaseSubscriberCount() {
        if (this.subscriberCount > 0) {
            this.subscriberCount--;
        }
    }
}
