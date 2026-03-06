package com.socialplatform.core.domain.model;

import java.time.LocalDate;

import com.socialplatform.core.domain.enums.AccountVisibility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "profiles", indexes = {
        @Index(name = "idx_profile_user_id", columnList = "user_id"),
        @Index(name = "idx_profile_full_name", columnList = "first_name, last_name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class Profile extends BaseEntity {

    @Setter
    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Setter
    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "account_visibility", nullable = false)
    private AccountVisibility accountVisibility = AccountVisibility.PUBLIC;

    @Setter
    @Size(max = 500)
    @Column(length = 500)
    private String bio;

    @Setter
    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Setter
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Setter
    @Column(name = "cover_picture_url")
    private String coverPictureUrl;

    @Setter
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Setter
    @Size(max = 100)
    @Column(length = 100)
    private String occupation;

    @Setter
    @Size(max = 1000)
    @Column(length = 1000)
    private String interests;

    @Setter
    @Column(length = 255)
    private String website;

    @Setter
    @Column(length = 150)
    private String location;

    @Setter
    @Column(length = 255)
    private String education;

    @Setter
    @Column(length = 255)
    private String workplace;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public String getFullName() {
        if (firstName == null && lastName == null)
            return "Unnamed User";

        String first = (firstName != null) ? firstName : "";
        String last = (lastName != null) ? lastName : "";

        return (first + " " + last).strip();
    }

    public boolean isPublic() {
        return AccountVisibility.PUBLIC.equals(this.accountVisibility);
    }
}
