## Contributors

Melina Tokka


## Technical Details
### Technologies Used
- **Programming Language**: Java,Python
- **Framework**:  Spring Boot
- **Database**: MySQL
\- **Other Libraries**: Pandas, Thymeleaf, Bootstrap, D3.js



### Installation Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/mtokka/mye030-project.git
2. Navigate to the project directory:
      ```bash
   cd mye030-project

3. Transform the data:
      ```bash
   cd files
   python3 transform.py
      
4. Create the database and load the clean files:
      ```bash
      
   mysql -u username -p
   source files/create_tables.sql;
   source files/load.sql;
      
5. Set up environment variables:
   Edit this file with your MySQL configuration:
   ```bash
    src/main/resources/application.properties

6. Start the application:
      ```bash
   ./mvnw spring-boot:run

7. Access the application:
    The application will be available at http://localhost:8080
