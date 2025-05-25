Running the Software:
    1. Obtain the project by either cloning the repository or unzipping the folder.
    2. User create_database.sql to create and populate the database tables
    3. Use Maven to clean and compile the project.
    4. Deploy the "target/demo" folder onto a server.
    5. Navigate to "target/demo/" in the browser to reach the landing page.

Login / Registration:
    1. Upon reaching the landing page, you will have 2 options: Login and Register
    2. Two Types of registeration is available
        2.1. You can register a user account by filling in your details normally.
        2.2. If you want to register a staff account, the staff pin is needed. This pin is '0000'.
    3. If you want to login as admin, please enter the following details in the login page:
        Email: admin@iotbay.com
        Password: admin123

Abilities:
    1. Customers have the ability to view their profile, view the device catalog, and view their access logs.
    2. Staff users have the ability to edit device details within the catalog.
    3. Admin is able to create, update and delete customers, staff and devices. As well as update account activation status.

MySQL
1.Download MySQL MySQL :: Download MySQL Installer
2.Set up the mysql server, make sure port is "3306" and password is "1111"
3.Download the MySQL extension in vscode
4.Go to the database tab 
5. Create a new connection with this config:
6. In the new connection, there should be an option to add a new database using sql. Copy everything from the create_database.sql file (on the git repo), paste it and then run the sql.