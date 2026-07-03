package com.example.lab.analysis;

import com.example.lab.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "analyses")
public class Analysis {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "test_name", nullable = false)
	private String testName;

	@Column(name = "result_value", nullable = false)
	private String resultValue;

	@Column(name = "unit")
	private String unit;

	@Column(name = "reference_range")
	private String referenceRange;

	@Column(name = "taken_at", nullable = false)
	private LocalDate takenAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected Analysis() {
	}

	public Analysis(User user, String testName, String resultValue, String unit, String referenceRange,
			LocalDate takenAt) {
		this.user = user;
		this.testName = testName;
		this.resultValue = resultValue;
		this.unit = unit;
		this.referenceRange = referenceRange;
		this.takenAt = takenAt;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getTestName() {
		return testName;
	}

	public String getResultValue() {
		return resultValue;
	}

	public String getUnit() {
		return unit;
	}

	public String getReferenceRange() {
		return referenceRange;
	}

	public LocalDate getTakenAt() {
		return takenAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
