# DSA Adventure

**DSA Adventure** is an interactive Java Swing educational game designed to help learners practise fundamental Data Structures and Algorithms through short, hands-on challenges.

Instead of learning DSA concepts only through theory, players solve interactive mini-games based on common algorithms and data structures while earning points and managing their lives.

## Mini-Games

### Bubble Sort

Sort an array by selecting adjacent elements and performing valid Bubble Sort swaps.

**Concepts covered:**

* Arrays
* Sorting
* Bubble Sort
* Comparisons and swaps

### Stack Challenge

Build a stack that matches the target arrangement using **PUSH** and **POP** operations.

**Concepts covered:**

* Stack
* LIFO (Last In, First Out)
* Push and Pop operations

### Queue Challenge

Enqueue tasks in the correct order and dequeue them to match the target sequence.

**Concepts covered:**

* Queue
* FIFO (First In, First Out)
* Enqueue and Dequeue operations

### Binary Search

Find a hidden target within a sorted array by repeatedly narrowing down the search range.

**Concepts covered:**

* Arrays
* Searching
* Binary Search
* Divide-and-conquer logic

## Game System

DSA Quest includes a shared game state that tracks:

* Total score
* Player lives
* Completed mini-games
* Game progress

Players can return to the main menu at any time, see their current progress, and reset the entire game when needed.

## Features

* Four interactive DSA mini-games
* Interactive graphical user interface
* Score tracking
* Life system
* Game completion tracking
* Randomised challenges
* Tutorial available for each game
* Reset and replay functionality
* Visual feedback for correct and incorrect actions
* Beginner-friendly approach to learning DSA

## Technologies Used

* **Java**
* **Java Swing**
* **Object-Oriented Programming**
* **Data Structures & Algorithms**

## Project Structure

```text
src/
└── main/
    └── java/
        └── com/
            └── dsagame/
                ├── Main.java
                ├── GameState.java
                │
                ├── games/
                │   ├── BubbleSortGame.java
                │   ├── StackGame.java
                │   ├── QueueGame.java
                │   └── BinarySearchGame.java
                │
                └── ui/
                    └── MainMenuPanel.java
```

## How to Run

### Prerequisites

Make sure you have:

* Java JDK 17 or later
* An IDE such as IntelliJ IDEA, Eclipse, or VS Code with Java support

### Running the Project

1. Clone the repository:

```bash
git clone <your-repository-url>
```

2. Open the project in your preferred Java IDE.

3. Make sure the project is configured with JDK 17 or later.

4. Run:

```text
src/main/java/com/dsagame/Main.java
```

5. The DSA Quest main menu will open.

##  Objective

The goal of DSA Quest is to make learning fundamental Data Structures and Algorithms more engaging by turning algorithmic concepts into interactive challenges.

Players learn by **doing**, rather than simply reading about how an algorithm or data structure works.

## Future Improvements

Potential future additions include:

* More DSA mini-games
* Difficulty levels
* High-score system
* More advanced algorithms
* Achievement and badge system
* Sound effects and animations
* Persistent player progress
* Timed challenges
* Leaderboards

## Author

Developed as a Java-based Data Structures and Algorithms learning project.

---

**Learn DSA. Play the challenges. Master the algorithms. 🎮**
