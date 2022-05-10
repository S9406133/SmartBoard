package com.smartboard.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Menu {

	private ArrayList<User> users;
	private final Quote currentQuote;
	public static final Quote[] QUOTES = {
			new Quote("Do or do not. There is no try.", "Yoda"),
			new Quote("You must unlearn what you have learned.", "Yoda"),
			new Quote("Named must be your fear before banish it you can.", "Yoda"),
			new Quote("Fear is the path to the dark side. Fear leads to anger. Anger leads to hate. Hate leads to suffering.", "Yoda"),
			new Quote("Who's the more foolish: the fool or the fool who follows him?", "Obi-Wan Kenobi")
			};

	public Menu() {
		this.users = new ArrayList<>();

		Random rand = new Random();
		int randomInt = rand.nextInt(QUOTES.length);
		this.currentQuote = QUOTES[randomInt];

		try {
			this.users.add(new User("sim", "a", "Simon", "James"));
//			this.users.get(0).addSubItem("First");
//			this.users.get(0).addSubItem("Second");
//			this.users.get(0).addSubItem("Third");
//			this.users.get(0).addSubItem("Fourth");
//			this.users.get(0).getSubItem(0).addSubItem("Column1");
//			this.users.get(0).getSubItem(0).addSubItem("Column2");
//			this.users.get(0).getSubItem(0).addSubItem("Column3");
//			this.users.get(0).getSubItem(2).addSubItem("ColumnA");
//			this.users.get(0).getSubItem(2).addSubItem("ColumnB");
//			this.users.get(0).getSubItem(0).getSubItem(0).addSubItem("Task1");
//			this.users.get(0).getSubItem(0).getSubItem(0).addSubItem("Task2");
		} catch (IndexOutOfBoundsException | StringLengthException e1) {
			System.out.println(e1.getMessage());
		}
	}

	/**
	 * Initial Menu
	 */
	protected void loginMenu() {
		boolean exit = false;
		final String TITLE = "from Login Menu";
		List<String> options = List.of("Login (Existing Profile)", "Create new profile", "Exit");

		System.out.println("\n>>>  SMART BOARD  <<<\n");

		do {
			printMenu(TITLE, options);

			String input = readUserInput();

			switch (input) {
				case "1" -> login();
				case "2" -> createUser();
				case "3" -> {
					System.out.println("Good-Bye");
					exit = true;
				}
			}

		} while (!exit);
	}

	/**
	 * Method to create a new user list item
	 */
	private void createUser() {
		boolean exit = false;
		boolean exists;
		boolean createUser = false;
		String username;
		String password;
		String firstName;
		String lastName;

		do {
			do {
				exists = false;
				System.out.print("\nPlease enter your Username: ");
				username = readUserInput();
				if (usernameExists(username)) {
					System.out.print("This Username already exists");
					exists = true;
				}
			} while (exists);
			
			System.out.print("Please enter your Password: ");
			password = readUserInput();
			System.out.print("Please enter your First Name: ");
			firstName = readUserInput();
			System.out.print("Please enter your Last Name: ");
			lastName = readUserInput();

			System.out.printf("\nYou have entered:\nUsername: %s\nPassword: %s\nFirst Name: %s\nLast Name: %s",
					username, password, firstName, lastName);
			System.out.print("\n(A)ccept or (R)etry or (E)xit: ");
			String input = readUserInput().toLowerCase();

			switch (input) {
				case "a" -> {
					exit = true;
					createUser = true;
				}
				case "e" -> exit = true;
			}

		} while (!exit);

		if (createUser) {
			try {
				this.users.add(new User(username, password, firstName, lastName));
				User newUser = this.users.get(this.users.size() - 1);
				mainMenu(newUser);
			} catch (StringLengthException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Method to find if the username already exists in the users list
	 */
	private boolean usernameExists(String username) {
		boolean returnVal = false;
		
		for (User user : this.users) {
			if (username.equalsIgnoreCase(user.getName())) {
				returnVal = true;
				break;
			}
		}
		
		return returnVal;
	}

	/**
	 * Method to process login request
	 */
	private void login() {
		boolean exit = false;
		User validUser = null;

		do {
			String message = "No such Username";

			System.out.print("\nPlease enter your Username: ");
			String username = readUserInput();
			System.out.print("Please enter your Password: ");
			String password = readUserInput();

			for (User user : this.users) {
				if (user.getName().equalsIgnoreCase(username)) {
					if (user.validateLogin(username, password)) {
						validUser = user;
						exit = true;
						break;
					} else {
						message = "Invalid password";
					}
				}
			}

			if (!exit) {
				System.out.println(message + "...\n");
				System.out.print("(E)xit or (R)etry: ");
				String input = readUserInput();
				exit = input.equalsIgnoreCase("E");
			} else {
				mainMenu(validUser);
			}

		} while (!exit);

	}

	/**
	 * Main menu
	 */
	private void mainMenu(User user) {
		boolean exit = false;
		final String TITLE = "from Main Menu";
		List<String> options = List.of("Edit Profile", "Display Project Menu", "Log out");

		System.out.printf("\nWelcome %s %s\n", user.getFirstName(), user.getLastName());

		do {
			System.out.print("\n" + this.currentQuote);
			printMenu(TITLE, options);

			String stringInput = readUserInput();

			switch (stringInput) {
				case "1" -> { // Edit Profile
					System.out.println("\nEdit profile...\n");
					editUser(user);
				}
				case "2" -> // Project Menu
						projectMenu(user);
				case "3" -> // Logout
						exit = true;
				default -> System.out.println("\nPlease select a valid menu option.");
			}
		} while (!exit);

		System.out.println("\nGoodbye " + user.getFirstName());
	}

	/**
	 * Project menu
	 */
	private void projectMenu(User user) {
		boolean exit = false;
		final String TITLE = "from Project Menu";
		List<String> options = List.of("View & Edit Default Project Columns", "View All Project Boards / Set Default",
				"Create new Project Board", "Delete Project Board", "Main Menu");

		do {
			System.out.printf("\nLogged in as: %s %s", user.getFirstName(), user.getLastName());
			printMenu(TITLE, options);

			String stringInput = readUserInput();

			switch (stringInput) {
				case "1" -> { // View Project
					System.out.println("\nDisplay Project...\n");
					try {
						System.out.println("Default project: \n" + user.getDefaultProject().toString() + "\n");
						columnMenu(user.getDefaultProject());
					} catch (IndexOutOfBoundsException e) {
						System.out.println("No project boards created....\n");
					}
				}
				case "2" -> { // View All Projects
					System.out.println("\nDisplay all projects...\n");
					displayAllSubItems(user);
				}
				case "3" -> { // Create new project
					System.out.println("\nCreate project...\n");
					createSubItem(user);
				}
				case "4" -> { // Delete Project
					System.out.println("\nDelete project...\n");
					deleteSubItem(user);
				}
				case "5" -> // Main Menu
						exit = true;
				default -> System.out.println("\nPlease select a valid menu option.");
			}
		} while (!exit);
	}

	/**
	 * Column menu
	 */
	private void columnMenu(Project project) {
		boolean exit = false;
		Column currColumn = null;
		final String TITLE = "from Column Menu";
		List<String> options = List.of("View & Edit Tasks in Column", "View All Columns", "Create new Column",
				"Delete Column", "Project Menu");

		do {
			System.out.printf("\nEdit the columns for Project Board: %s", project.getName());
			printMenu(TITLE, options);

			String stringInput = readUserInput();

			switch (stringInput) {
				case "1" -> { // View column
					System.out.println("\nDisplay Column...\n");
					displayAllSubItems(project);
					System.out.print("Enter name of Column to display: ");
					String input = readUserInput();
					if (!input.isBlank()) {
						for (Column col : project.getSubItemList()) {
							if (input.equalsIgnoreCase(col.getName())) {
								currColumn = col;
								break;
							}
						}
					}
					if (currColumn == null) {
						System.out.println("No Column selected");
					} else {
						taskMenu(currColumn, project.getName());
					}
				}
				case "2" -> { // View All Columns
					System.out.println("\nDisplay all Columns...\n");
					displayAllSubItems(project);
				}
				case "3" -> { // Create new project
					System.out.println("\nCreate Column...\n");
					createSubItem(project);
				}
				case "4" -> { // Delete Project
					System.out.println("\nDelete column...\n");
					deleteSubItem(project);
				}
				case "5" -> // Project Menu
						exit = true;
				default -> System.out.println("\nPlease select a valid menu option.");
			}
		} while (!exit);
	}

	/**
	 * Task menu
	 */
	private void taskMenu(Column column, String projectName) {
		boolean exit = false;
		final String TITLE = "from Task Menu";
		List<String> options = List.of("View & Edit Task", "View All Tasks", "Create new Task", "Delete Task",
				"Column Menu");

		do {
			System.out.printf("\nEdit the tasks for Column: %s -> in Project: %s", column.getName(), projectName);
			printMenu(TITLE, options);

			String stringInput = readUserInput();

			switch (stringInput) {
			case "1": // View Task
				System.out.println("\nDisplay Task...\n");
				displayAllSubItems(column);
				System.out.print("Enter name of Column to display: ");
				String input = readUserInput();
				if (!input.isBlank()) {
					for (Task task : column.getSubItemList()) {
						if (input.equalsIgnoreCase(task.getName())) {
							System.out.printf("\nTask: %s\nDescription: %s\nDue Date: %s\n", task.getName(),
									task.getDescription(), task.getDueDate().toString());
							break;
						}
					}
				}
				break;
			case "2": // View All Tasks
				System.out.println("\nDisplay all tasks...\n");
				displayAllSubItems(column);
				break;
			case "3": // Create new Task
				System.out.println("\nCreate task...\n");
				createSubItem(column);
				break;
			case "4": // Delete task
				System.out.println("\nDelete task...\n");
				deleteSubItem(column);
				break;
			case "5": // Column Menu
				exit = true;
			default:
				System.out.println("\nPlease select a valid menu option.");
			}
		} while (!exit);
	}

	/**
	 * Method to edit user details
	 */
	private void editUser(User user) {
		boolean exit = false;
		boolean apply = false;
		String firstName;
		String lastName;

		System.out.printf("\n>> Edit Profile for %s<<\n", user.getName());

		do {
			System.out.println("\nCurrent First Name is " + user.getFirstName());
			System.out.print("Please enter new First Name: ");
			firstName = readUserInput();
			System.out.println("Current Last Name is " + user.getLastName());
			System.out.print("Please enter new Last Name: ");
			lastName = readUserInput();

			System.out.printf("\nYou have entered:\nFirst Name: %s\nLast Name: %s", firstName, lastName);
			System.out.print("\n(A)ccept or (R)etry or (E)xit: ");
			String input = readUserInput().toLowerCase();

			switch (input) {
				case "a" -> {
					exit = true;
					apply = true;
				}
				case "e" -> exit = true;
			}

		} while (!exit);

		if (apply) {
			try {
				user.setFirstName(firstName);
				user.setLastName(lastName);
			} catch (StringLengthException e) {
				System.out.println(e.getMessage());
			}
		}

	}

	/**
	 * Method to display all subitems of the boarditem passed in
	 */
	private void displayAllSubItems(BoardItem<?> boardItem) {

		if (boardItem.getListSize() <= 0) {
			System.out.println("No items to view....\n");
		} else {

			for (Object item : boardItem.getSubItemList()) {
				if (boardItem instanceof User) {
					if (item == ((User) boardItem).getDefaultProject()) {
						System.out.println("** DEFAULT **");
					}
				}
				System.out.println(item.toString() + "\n");
			}

			if (boardItem instanceof User) {
				System.out.print("Set new default project board (Y/N): ");
				String input = readUserInput();
				if (input.equalsIgnoreCase("y")) {
					setDefaultProject((User) boardItem);
				}
			}
		}
	}

	/**
	 * Method to set the default project
	 */
	private void setDefaultProject(User user) {

		boolean newDefaultSet = false;

		System.out.print("Enter name of new default project: ");
		String newDefaultName = readUserInput();

		for (int i = 0; i < user.getListSize(); i++) {
			if (newDefaultName.equalsIgnoreCase(user.getSubItem(i).getName())) {
				try {
					user.toggleDefaultProject(i);
					newDefaultSet = true;
					break;
				} catch (IndexOutOfBoundsException e) {
					System.out.println(e.getMessage());
				}
			}
		}

		String msg = (newDefaultSet) ? "New default set:\n" + user.getDefaultProject()
				: "Error: " + newDefaultName + " not found";

		System.out.println(msg);
	}

	/**
	 * Method to create a new boarditem to the passed in boarditems' subitem list
	 */
	private void createSubItem(BoardItem<?> boardItem) {
		String message = "Item NOT Added";
		System.out.print("Please enter new Item Name: ");
		String subItemName = readUserInput();

		if (!subItemName.isBlank()) {
			try {
				boardItem.addSubItem(subItemName);

				if (boardItem instanceof Column) {
					System.out.print("Please enter new Task Description: ");
					String description = readUserInput();
					((Column) boardItem).getSubItem(boardItem.getListSize() - 1).setDescription(description);
				}

				message = "Item Added - " + subItemName;

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.println(message);
	}

	/**
	 * Method to delete a subitem from the list
	 */
	private void deleteSubItem(BoardItem<?> boardItem) {
		boolean itemRemoved = false;

		for (Object item : boardItem.getSubItemList()) {
			System.out.println(((BoardItem<?>) item).getName());
		}

		System.out.print("Please enter Item Name to delete: ");
		String itemName = readUserInput();

		for (Object item : boardItem.getSubItemList()) {
			if (itemName.equalsIgnoreCase(((BoardItem<?>) item).getName())) {
				itemRemoved = boardItem.removeSubItem((BoardItem) item);
				break;
			}
		}

		String msg = (itemRemoved) ? "Item successfully removed" : "Error removing item";

		System.out.println(msg);
	}

	/**
	 * Method to print the menus
	 */
	private static void printMenu(String title, Iterable<String> optionsList) {
		String banner = new String(new char[50]).replace('\u0000', '=');
		System.out.println("\n" + banner + "\n>> Select " + title + " <<\n" + banner);
		int i = 1;
		for (String option : optionsList) {
			System.out.printf("   %d) %s\n", i, option);
			i++;
		}
		System.out.print("Please select: ");
	}

	private static String readUserInput() {
		Scanner sc = new Scanner(System.in);
		return sc.nextLine();
	}
}
