package com.smartboard.model;
import java.time.LocalDateTime;

public class Task extends BoardItem<String> {

	private String description;
	private LocalDateTime dueDate;

	public Task(String name) throws StringLengthException {
		super(name);

		this.description = "Provide a description";
		this.dueDate = LocalDateTime.now();
	}

	@Override
	protected void addSubItem(String subItemName) {
		this.subItems.add(subItemName);
	}

	protected String getDescription() {
		return description;
	}

	protected void setDescription(String description) {
		if (!description.isBlank()) {
			this.description = description;
		}
	}

	protected LocalDateTime getDueDate() {
		return this.dueDate;
	}

	protected void setDueDate(LocalDateTime newDate) {
		if (newDate.isAfter(LocalDateTime.now())) {
			this.dueDate = newDate;
		}
	}

	@Override
	public String toString() {
		return String.format("    << Task: %s\n        Description: %s\n        Due Date: %s >>", this.name,
				this.description, this.dueDate.toLocalDate());
	}

}
