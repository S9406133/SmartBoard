/**
 * This class holds the static utility methods used to
 * update a Project instance and related Database record
 */

package com.smartboard.model;

import org.jetbrains.annotations.NotNull;

public class Project_Utils {

    /**
     * Creates and adds a new Project to a User and creates a database record of the Project
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    public static Project addNewProject(String name) throws StringLengthException {
        Project newProject = User_Utils.currentUser.addSubItem(name);
        DB_Utils.InsertNewProject(newProject);
        return newProject;
    }

    /**
     * Updates the name of the Project instance and the database record
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    public static void updateProjectName(@NotNull Project project, String name) throws StringLengthException {
        project.setName(name);
        DB_Utils.UpdateProject(project);
    }

    /**
     * Updates the default attribute of the Project instance and the database record
     */
    public static void updateProjectDefault(@NotNull Project project, boolean isDefault) {
        project.setDefault(isDefault);
        DB_Utils.UpdateProject(project);
    }
}
