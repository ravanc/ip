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
capitalisation and matches any part of a description, so `find book` also finds "Bookshop".
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

## Feature ABC

// Feature details


## Feature XYZ

// Feature details