# UI Test Plan

## Program

- Working directory: .
- Compile verity.command: `javac -d out/production/ip src/main/java/*.java`
- Run verity.command: `java -cp out/production/ip verity.Verity`

## Test Case 1: Exit verity.command

- Aim: Verify that verity.Verity starts normally and exits when the user enters `bye`.

### Inputs

```text
bye
```

### Expected output

```text
____________________________________________________________
V   V  EEEEE  RRRR   IIIII  TTTTT  Y   Y
V   V  E      R   R    I      T     Y Y
 V V   EEEE   RRRR     I      T      Y
  V    E      R R      I      T      Y
  V    EEEEE  R  RR  IIIII    T      Y

Hello! I'm verity.Verity.
I speak only the truth.
What can I do for you?
____________________________________________________________

____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 2: Empty list

- Aim: Verify that `list` displays the task-list heading and no tasks when no tasks have been added.

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
V   V  EEEEE  RRRR   IIIII  TTTTT  Y   Y
V   V  E      R   R    I      T     Y Y
 V V   EEEE   RRRR     I      T      Y
  V    E      R R      I      T      Y
  V    EEEEE  R  RR  IIIII    T      Y

Hello! I'm verity.Verity.
I speak only the truth.
What can I do for you?
____________________________________________________________

____________________________________________________________

    Here are the tasks in your list:

____________________________________________________________

____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________

```
