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
            admin.setUsername("Nitin Mane");
            admin.setEmail("admin@placement.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            //System.out.println("Default admin user created: admin@placement.com / admin123");
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

            // Additional OOPS Questions (12 more)
            questionRepository.save(new Question(
                    "What is the difference between method overloading and method overriding?",
                    "Overloading is compile-time, overriding is runtime",
                    "Overriding is compile-time, overloading is runtime",
                    "They are the same",
                    "No difference",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which keyword is used to refer to the current object in Java?",
                    "this",
                    "super",
                    "self",
                    "current",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is a static method in Java?",
                    "A method that belongs to the class, not instances",
                    "A method that cannot be overridden",
                    "A method that is always public",
                    "A method that returns void",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which OOP concept allows a child class to provide a specific implementation of a method?",
                    "Method Overloading",
                    "Method Overriding",
                    "Encapsulation",
                    "Abstraction",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is an abstract class in Java?",
                    "A class that cannot be instantiated",
                    "A class with only static methods",
                    "A class with no methods",
                    "A class that is always public",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the 'super' keyword in Java?",
                    "To call parent class methods and constructors",
                    "To create a new object",
                    "To delete an object",
                    "To stop execution",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which access modifier allows access within the same package?",
                    "private",
                    "public",
                    "protected",
                    "default",
                    "D"
            ));
            questionRepository.save(new Question(
                    "What is polymorphism in OOP?",
                    "Ability of objects to take multiple forms",
                    "Hiding implementation details",
                    "Inheriting from parent class",
                    "Creating multiple objects",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the difference between an interface and an abstract class?",
                    "Interface supports multiple inheritance, abstract class doesn't",
                    "Abstract class supports multiple inheritance, interface doesn't",
                    "No difference",
                    "Interface can have constructors",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which keyword is used to implement an interface in Java?",
                    "extends",
                    "implements",
                    "inherits",
                    "uses",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is composition in OOP?",
                    "A class containing objects of other classes",
                    "Inheriting from multiple classes",
                    "Overriding methods",
                    "Creating static methods",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of a constructor?",
                    "To initialize objects",
                    "To destroy objects",
                    "To call methods",
                    "To return values",
                    "A"
            ));

            // Additional Java 8 Features (12 more)
            questionRepository.save(new Question(
                    "What is a functional interface in Java 8?",
                    "An interface with exactly one abstract method",
                    "An interface with multiple methods",
                    "An interface with no methods",
                    "An abstract class",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which method is used to find the first element in a Stream?",
                    "first()",
                    "findFirst()",
                    "getFirst()",
                    "start()",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What does the reduce() method do in Java 8 Streams?",
                    "Reduces the stream to a single value",
                    "Filters elements",
                    "Maps elements",
                    "Sorts elements",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which interface represents a supplier of results in Java 8?",
                    "Function",
                    "Consumer",
                    "Supplier",
                    "Predicate",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is method reference in Java 8?",
                    "A shorthand notation for lambda expressions",
                    "A way to call methods",
                    "A type of variable",
                    "A new keyword",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which method is used to skip elements in a Stream?",
                    "skip()",
                    "jump()",
                    "pass()",
                    "ignore()",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What does the flatMap() method do in Java 8?",
                    "Maps and flattens nested collections",
                    "Filters elements",
                    "Sorts elements",
                    "Reduces elements",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which interface represents a predicate in Java 8?",
                    "Function<T, Boolean>",
                    "Predicate<T>",
                    "Consumer<T>",
                    "Supplier<T>",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of Collectors.groupingBy()?",
                    "To group elements by a classifier",
                    "To filter elements",
                    "To sort elements",
                    "To map elements",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which method is used to check if all elements match a predicate?",
                    "allMatch()",
                    "anyMatch()",
                    "noneMatch()",
                    "checkMatch()",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What does Optional.orElse() do?",
                    "Returns value if present, otherwise returns default",
                    "Throws exception if empty",
                    "Returns null",
                    "Creates new Optional",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which method is used to convert Stream to an array?",
                    "toArray()",
                    "asArray()",
                    "array()",
                    "convertArray()",
                    "A"
            ));

            // Additional HTML Questions (12 more)
            questionRepository.save(new Question(
                    "Which HTML tag is used to define a form?",
                    "<form>",
                    "<input>",
                    "<submit>",
                    "<button>",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the <div> tag?",
                    "To create a container/division",
                    "To create a table",
                    "To create a link",
                    "To create a list",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which attribute is used to make an input field required?",
                    "required",
                    "mandatory",
                    "must",
                    "needed",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the <span> tag?",
                    "To create an inline container",
                    "To create a block container",
                    "To create a table",
                    "To create a form",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which HTML5 element is used for article content?",
                    "<article>",
                    "<section>",
                    "<content>",
                    "<text>",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the <header> tag?",
                    "To define a header section",
                    "To create a table header",
                    "To create a link",
                    "To create a form",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which HTML tag is used to create a line break?",
                    "<br>",
                    "<break>",
                    "<lb>",
                    "<newline>",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the <footer> tag?",
                    "To define a footer section",
                    "To create a table footer",
                    "To create a link",
                    "To stop execution",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which attribute is used to specify the input type?",
                    "type",
                    "input",
                    "kind",
                    "format",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the <section> tag?",
                    "To define a section in a document",
                    "To create a table",
                    "To create a link",
                    "To create a form",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which HTML tag is used to create a horizontal rule?",
                    "<hr>",
                    "<line>",
                    "<rule>",
                    "<separator>",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the <aside> tag?",
                    "To define content aside from main content",
                    "To create a table",
                    "To create a link",
                    "To create a form",
                    "A"
            ));

            // Additional JavaScript Questions (18 more)
            questionRepository.save(new Question(
                    "What is the difference between let and var?",
                    "let has block scope, var has function scope",
                    "var has block scope, let has function scope",
                    "No difference",
                    "let is deprecated",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log(typeof undefined)?",
                    "undefined",
                    "null",
                    "object",
                    "string",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which method is used to remove the first element from an array?",
                    "shift()",
                    "unshift()",
                    "pop()",
                    "push()",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log('5' - 2)?",
                    "3",
                    "52",
                    "Error",
                    "undefined",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is a callback function?",
                    "A function passed as an argument to another function",
                    "A function that returns a value",
                    "A function that is called immediately",
                    "A function that is never called",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log([] + [])?",
                    "[]",
                    "''",
                    "Error",
                    "undefined",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Which method is used to find an element in an array?",
                    "find()",
                    "search()",
                    "locate()",
                    "get()",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log(0.1 + 0.2)?",
                    "0.3",
                    "0.30000000000000004",
                    "Error",
                    "undefined",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the filter() method?",
                    "To create a new array with elements that pass a test",
                    "To remove all elements",
                    "To sort elements",
                    "To reverse elements",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log(null == undefined)?",
                    "true",
                    "false",
                    "Error",
                    "null",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which method is used to join array elements into a string?",
                    "join()",
                    "concat()",
                    "merge()",
                    "combine()",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log(typeof NaN)?",
                    "number",
                    "NaN",
                    "undefined",
                    "null",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the reduce() method?",
                    "To reduce array to a single value",
                    "To filter elements",
                    "To sort elements",
                    "To reverse elements",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log('5' + 2)?",
                    "52",
                    "7",
                    "Error",
                    "undefined",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Which method is used to check if an array includes a value?",
                    "includes()",
                    "contains()",
                    "has()",
                    "check()",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log([] == false)?",
                    "true",
                    "false",
                    "Error",
                    "undefined",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the purpose of the slice() method?",
                    "To extract a section of an array",
                    "To remove elements",
                    "To add elements",
                    "To sort elements",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: console.log(typeof [])?",
                    "object",
                    "array",
                    "undefined",
                    "null",
                    "A"
            ));

            // Additional Logical Reasoning Questions (12 more)
            questionRepository.save(new Question(
                    "Complete the series: 1, 4, 9, 16, 25, ?",
                    "36",
                    "35",
                    "34",
                    "37",
                    "A"
            ));
            questionRepository.save(new Question(
                    "If all birds can fly and penguin is a bird, then:",
                    "Penguin can fly",
                    "Penguin cannot fly",
                    "Cannot be determined",
                    "Penguin is not a bird",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Complete the series: 5, 10, 20, 40, ?",
                    "60",
                    "70",
                    "80",
                    "90",
                    "C"
            ));
            questionRepository.save(new Question(
                    "If APPLE is coded as 1225165125, how is ORANGE coded?",
                    "15181147145",
                    "15181147146",
                    "15181147147",
                    "15181147148",
                    "A"
            ));
            questionRepository.save(new Question(
                    "Find the odd one out: 2, 4, 8, 16, 32, 64",
                    "All are even",
                    "All are powers of 2",
                    "None are odd",
                    "No odd one out",
                    "D"
            ));
            questionRepository.save(new Question(
                    "If a clock shows 6:30, what is the angle between the hands?",
                    "0 degrees",
                    "15 degrees",
                    "30 degrees",
                    "45 degrees",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Complete the series: 1, 3, 6, 10, 15, ?",
                    "20",
                    "21",
                    "22",
                    "23",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If RED is coded as 1854, how is BLUE coded?",
                    "212215",
                    "212125",
                    "212225",
                    "212235",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What comes next: 2, 5, 11, 23, 47, ?",
                    "94",
                    "95",
                    "96",
                    "97",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If 3 apples cost $6, how much do 5 apples cost?",
                    "$8",
                    "$10",
                    "$12",
                    "$15",
                    "B"
            ));
            questionRepository.save(new Question(
                    "Complete the series: A, D, G, J, ?",
                    "K",
                    "L",
                    "M",
                    "N",
                    "C"
            ));
            questionRepository.save(new Question(
                    "If today is Monday, what day will it be after 25 days?",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "D"
            ));

            // Additional General Aptitude Questions (12 more)
            questionRepository.save(new Question(
                    "What is 15% of 300?",
                    "40",
                    "45",
                    "50",
                    "55",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If a car travels 240 km in 3 hours, what is its average speed?",
                    "70 km/h",
                    "80 km/h",
                    "90 km/h",
                    "100 km/h",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the cube of 5?",
                    "100",
                    "125",
                    "150",
                    "175",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If 4x - 8 = 12, what is the value of x?",
                    "4",
                    "5",
                    "6",
                    "7",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the LCM of 12 and 18?",
                    "30",
                    "36",
                    "42",
                    "48",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If a square has side length 6, what is its area?",
                    "30",
                    "36",
                    "42",
                    "48",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is 2^5?",
                    "30",
                    "32",
                    "34",
                    "36",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If 20% of a number is 40, what is the number?",
                    "180",
                    "200",
                    "220",
                    "240",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the perimeter of a rectangle with length 10 and width 6?",
                    "30",
                    "32",
                    "34",
                    "36",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the GCD of 24 and 36?",
                    "10",
                    "12",
                    "14",
                    "16",
                    "B"
            ));
            questionRepository.save(new Question(
                    "If a number is increased by 25% and then decreased by 20%, what is the net change?",
                    "No change",
                    "5% increase",
                    "5% decrease",
                    "Cannot determine",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the sum of first 10 natural numbers?",
                    "50",
                    "55",
                    "60",
                    "65",
                    "B"
            ));

            // Additional Pseudo code and Guess Output Questions (18 more)
            questionRepository.save(new Question(
                    "What is the output: int x = 10; int y = x++; print(x + ' ' + y);",
                    "10 10",
                    "11 10",
                    "10 11",
                    "11 11",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: String s = 'Test'; print(s.charAt(0));",
                    "T",
                    "t",
                    "0",
                    "Error",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: int[] arr = {1,2,3}; print(arr.length);",
                    "0",
                    "2",
                    "3",
                    "4",
                    "C"
            ));
            questionRepository.save(new Question(
                    "What is the output: int x = 5; print(x > 3 && x < 10);",
                    "true",
                    "false",
                    "Error",
                    "5",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: String s = 'Hello'; print(s.toUpperCase());",
                    "HELLO",
                    "hello",
                    "Hello",
                    "Error",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: int x = 10; if(x > 5) print('Yes'); else print('No');",
                    "Yes",
                    "No",
                    "Error",
                    "10",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: for(int i=1; i<=3; i++) print(i);",
                    "123",
                    "012",
                    "1234",
                    "Error",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: int x = 10; int y = 20; print(Math.max(x, y));",
                    "10",
                    "20",
                    "30",
                    "Error",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: String s = 'Java'; print(s.indexOf('a'));",
                    "0",
                    "1",
                    "2",
                    "-1",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: int x = 15; print(x % 4);",
                    "3",
                    "4",
                    "5",
                    "Error",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: boolean a = true; boolean b = false; print(a || b);",
                    "true",
                    "false",
                    "Error",
                    "null",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: int[] arr = {5, 10, 15}; print(arr[1]);",
                    "5",
                    "10",
                    "15",
                    "Error",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: String s = 'Test'; print(s.length());",
                    "3",
                    "4",
                    "5",
                    "Error",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: int x = 8; print(x / 2);",
                    "3",
                    "4",
                    "5",
                    "Error",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: int x = 5; print(x * 2 + 1);",
                    "10",
                    "11",
                    "12",
                    "13",
                    "B"
            ));
            questionRepository.save(new Question(
                    "What is the output: String s = 'Hello'; print(s.substring(0, 2));",
                    "He",
                    "Hel",
                    "Hell",
                    "Hello",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: int x = 10; print(x == 10);",
                    "true",
                    "false",
                    "10",
                    "Error",
                    "A"
            ));
            questionRepository.save(new Question(
                    "What is the output: int i = 0; do { i++; } while(i < 3); print(i);",
                    "2",
                    "3",
                    "4",
                    "Error",
                    "B"
            ));

            System.out.println("Sample questions initialized: " + questionRepository.count() + " questions added");
        }
    }
}

