package com.campusplacement.config;

import com.campusplacement.entity.Question;
import com.campusplacement.entity.Role;
import com.campusplacement.entity.User;
import com.campusplacement.repository.QuestionRepository;
import com.campusplacement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
        initializeSampleQuestions();
    }

    private void initializeAdminUser() {
        Optional<User> adminUser = userRepository.findByEmail("admin@placement.com");
        if (!adminUser.isPresent()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@placement.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@placement.com / admin123");
        }
    }

    private void initializeSampleQuestions() {
        if (questionRepository.count() == 0) {
            // OOPS Questions
            questionRepository.save(new Question(
                    "What is the main principle of Object-Oriented Programming that allows a class to inherit properties from another class?",
                    "Encapsulation",
                    "Inheritance",
                    "Polymorphism",
                    "Abstraction",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Which OOP concept allows multiple methods to have the same name but different parameters?",
                    "Method Overriding",
                    "Method Overloading",
                    "Inheritance",
                    "Encapsulation",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the access modifier that allows a member to be accessed only within its own class?",
                    "public",
                    "private",
                    "protected",
                    "default",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Which keyword is used to prevent a class from being inherited in Java?",
                    "static",
                    "final",
                    "abstract",
                    "private",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the relationship between a class and an object?",
                    "Object is an instance of a class",
                    "Class is an instance of an object",
                    "They are the same",
                    "No relationship",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which OOP principle is demonstrated when we hide internal implementation details?",
                    "Inheritance",
                    "Polymorphism",
                    "Encapsulation",
                    "Abstraction",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is the process of creating a new class from an existing class called?",
                    "Polymorphism",
                    "Inheritance",
                    "Encapsulation",
                    "Abstraction",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Which method is automatically called when an object is created in Java?",
                    "main()",
                    "Constructor",
                    "getter()",
                    "setter()",
                    "B"
            ));

            // Java 8 Features
            questionRepository.save(new Question(
                    "Which Java 8 feature allows you to pass behavior as a parameter?",
                    "Lambda Expressions",
                    "Streams API",
                    "Optional",
                    "Default Methods",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the Stream API in Java 8?",
                    "To handle file operations",
                    "To process collections in a functional style",
                    "To manage threads",
                    "To handle exceptions",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Which interface was introduced in Java 8 to represent a function that accepts one argument and produces a result?",
                    "Function",
                    "Consumer",
                    "Supplier",
                    "Predicate",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What does Optional class in Java 8 help to avoid?",
                    "NullPointerException",
                    "ClassNotFoundException",
                    "IOException",
                    "ArrayIndexOutOfBoundsException",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which Java 8 feature allows interfaces to have method implementations?",
                    "Abstract methods",
                    "Default methods",
                    "Static methods",
                    "Private methods",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output of: Stream.of(1,2,3).filter(x -> x > 1).count()?",
                    "0",
                    "1",
                    "2",
                    "3",
                    "C"
            ));
            questionRepository.save(new Question(
                    "Which method is used to convert a Stream to a List in Java 8?",
                    "toList()",
                    "collect(Collectors.toList())",
                    "asList()",
                    "list()",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What does the forEach() method do in Java 8 Streams?",
                    "Filters elements",
                    "Maps elements",
                    "Performs an action for each element",
                    "Reduces elements",
                    "C"
            ));

            // HTML Questions
            questionRepository.save(new Question(
                    "Which HTML tag is used to create a hyperlink?",
                    "<link>",
                    "<a>",
                    "<href>",
                    "<url>",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What does HTML stand for?",
                    "HyperText Markup Language",
                    "HighText Markup Language",
                    "HyperText Markdown Language",
                    "HighText Markdown Language",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which HTML tag is used to define an unordered list?",
                    "<ol>",
                    "<ul>",
                    "<list>",
                    "<li>",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the correct HTML element for the largest heading?",
                    "<h6>",
                    "<heading>",
                    "<h1>",
                    "<head>",
                    "C"
            ));
            questionRepository.save(new Question(
                    "Which attribute is used to provide alternative text for an image?",
                    "src",
                    "alt",
                    "title",
                    "href",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Which HTML5 element is used to define navigation links?",
                    "<nav>",
                    "<navigation>",
                    "<menu>",
                    "<links>",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the <meta> tag in HTML?",
                    "To create metadata about the document",
                    "To create a table",
                    "To create a form",
                    "To create a link",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which HTML tag is used to create a table row?",
                    "<tr>",
                    "<td>",
                    "<table>",
                    "<th>",
                    "A"
            ));

            // JavaScript Questions
            questionRepository.save(new Question(
                    "Which keyword is used to declare a variable in JavaScript that cannot be reassigned?",
                    "var",
                    "let",
                    "const",
                    "static",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is the result of: typeof null?",
                    "null",
                    "undefined",
                    "object",
                    "boolean",
                    "C"
            ));
            questionRepository.save(new Question(
                    "Which method is used to add an element to the end of an array in JavaScript?",
                    "push()",
                    "pop()",
                    "shift()",
                    "unshift()",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What does the === operator do in JavaScript?",
                    "Assigns a value",
                    "Compares value and type",
                    "Compares only value",
                    "Checks if undefined",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Which JavaScript method is used to convert a string to a number?",
                    "toNumber()",
                    "parseInt()",
                    "convert()",
                    "number()",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is a closure in JavaScript?",
                    "A function that has access to variables in its outer scope",
                    "A way to close a browser window",
                    "A method to stop execution",
                    "A type of loop",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which method is used to remove the last element from an array?",
                    "push()",
                    "pop()",
                    "shift()",
                    "unshift()",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output of: console.log(2 + '2')?",
                    "4",
                    "22",
                    "Error",
                    "undefined",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Which JavaScript feature allows asynchronous operations?",
                    "Promises",
                    "Loops",
                    "Functions",
                    "Variables",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What does the map() method do in JavaScript arrays?",
                    "Creates a new array with results of calling a function",
                    "Filters elements",
                    "Reduces elements",
                    "Sorts elements",
                    "A"
            ));

            // Logical Reasoning Questions
            questionRepository.save(new Question(
                    "If all roses are flowers and some flowers are red, which statement is definitely true?",
                    "All roses are red",
                    "Some roses are red",
                    "No roses are red",
                    "Cannot be determined",
                    "D"
            ));
            questionRepository.save(new Question(
                    "Complete the series: 2, 6, 12, 20, 30, ?",
                    "40",
                    "42",
                    "44",
                    "46",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If Monday is the first day, what day is the 100th day?",
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Sunday",
                    "B"
            ));
            questionRepository.save(new Question(
                    "A clock shows 3:15. What is the angle between the hour and minute hands?",
                    "0 degrees",
                    "7.5 degrees",
                    "15 degrees",
                    "30 degrees",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If CAT is coded as 3120, how is DOG coded?",
                    "4157",
                    "4156",
                    "4158",
                    "4159",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Find the odd one out: Apple, Mango, Orange, Carrot",
                    "Apple",
                    "Mango",
                    "Orange",
                    "Carrot",
                    "D"
            ));
            questionRepository.save(new Question(
                    "If 5 workers can build a wall in 10 days, how many days will 10 workers take?",
                    "5 days",
                    "10 days",
                    "15 days",
                    "20 days",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What comes next: A, C, E, G, ?",
                    "H",
                    "I",
                    "J",
                    "K",
                    "B"
            ));

            // General Aptitude Questions
            questionRepository.save(new Question(
                    "What is 25% of 200?",
                    "40",
                    "50",
                    "60",
                    "75",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If a train travels 120 km in 2 hours, what is its speed?",
                    "50 km/h",
                    "60 km/h",
                    "70 km/h",
                    "80 km/h",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the square root of 144?",
                    "10",
                    "11",
                    "12",
                    "13",
                    "C"
            ));
            questionRepository.save(new Question(
                    "If 3x + 5 = 20, what is the value of x?",
                    "3",
                    "4",
                    "5",
                    "6",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is the average of 10, 20, 30, 40, and 50?",
                    "25",
                    "30",
                    "35",
                    "40",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If the price of an item increases by 20% and then decreases by 20%, what is the net change?",
                    "No change",
                    "4% increase",
                    "4% decrease",
                    "Cannot determine",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is the next prime number after 17?",
                    "18",
                    "19",
                    "20",
                    "21",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If a rectangle has length 8 and width 5, what is its area?",
                    "13",
                    "26",
                    "40",
                    "45",
                    "C"
            ));

            // Pseudo code and Guess Output Questions
            questionRepository.save(new Question(
                    "What is the output of: int x = 5; x = x++ + ++x; print(x);",
                    "10",
                    "11",
                    "12",
                    "13",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What does this code output: for(int i=0; i<3; i++) { print(i); }",
                    "012",
                    "123",
                    "0123",
                    "Error",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: String s = 'Hello'; s = s + ' World'; print(s.length());",
                    "10",
                    "11",
                    "12",
                    "13",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: int[] arr = {1,2,3}; print(arr[arr.length]);",
                    "3",
                    "0",
                    "ArrayIndexOutOfBoundsException",
                    "null",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is the output: int a = 10; int b = 20; int c = (a > b) ? a : b; print(c);",
                    "10",
                    "20",
                    "30",
                    "0",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: String str = 'Java'; print(str.substring(1, 3));",
                    "Ja",
                    "av",
                    "ava",
                    "Error",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: int x = 10; if(x = 5) print('Yes'); else print('No');",
                    "Yes",
                    "No",
                    "Compilation Error",
                    "Runtime Error",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is the output: boolean flag = true; print(!flag && flag);",
                    "true",
                    "false",
                    "Error",
                    "null",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: int i = 0; while(i < 3) { i++; } print(i);",
                    "0",
                    "2",
                    "3",
                    "4",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is the output: int result = 10 / 3; print(result);",
                    "3",
                    "3.33",
                    "3.0",
                    "4",
                    "A"
            ));

            System.out.println("Sample questions initialized: " + questionRepository.count() + " questions added");
        }
    }
}

