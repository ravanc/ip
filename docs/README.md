# Shannon User Guide

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Finding tasks

Use `find` to pull up every task whose description contains a keyword. The search ignores
capitalization and matches any part of a description, so `find book` also finds "Bookshop".
Only the description is searched, not a deadline's date or an event's times.

Format: `find KEYWORD`

Everything after `find` is treated as one keyword, so `find team meeting` looks for that whole
phrase rather than for `team` or `meeting` separately.

Example: `find book`

```
Here are the matching tasks in your list:
1. [T][ ] read book
2. [D][ ] return Bookshop loan (by: Sep 01 2026)
```

If nothing matches, Shannon says `No matching tasks found!` instead.

Note that the numbers above count the matches, not the tasks in your full list. To mark or delete
one of them, run `list` first and use the number shown there.

## Marking and deleting several tasks at once

`mark`, `unmark` and `delete` each accept more than one task number, separated by spaces, so a
whole batch can be cleared in one line. The numbers are the ones shown by `list`.

Format: `delete TASK_NUMBER [TASK_NUMBER]...`

Example: `delete 1 3`

```
Noted. I've removed these tasks:
  [T][X] read book
  [D][ ] return book (by: Sep 01 2026)
Now you have 2 tasks in the list.
```

All the numbers are checked before anything changes, so if one of them is out of range Shannon
reports it and leaves every task alone &mdash; you never end up with half the line carried out.
Repeated numbers count once, so `delete 2 2` removes the second task and nothing else.

## Snoozing deadlines

Use `snooze` to push a deadline back when it is not going to happen on time. The task keeps its
place in your list and its done status; only its date moves.

Format: `snooze TASK_NUMBER [DAYS]`

`DAYS` is how many days to postpone by, and is 1 if left out, so `snooze 2` moves task 2 to the
next day. Only deadlines can be snoozed: todos have no date, and an event's times are kept as
the text you typed, so there is no date to move.

Example: `snooze 2 3`

```
Snoozed! I've pushed this back by 3 days:
  [D][ ] return book (by: Oct 04 2026)
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details