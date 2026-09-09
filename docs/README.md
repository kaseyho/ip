# Verity User Guide

Verity manages tasks and client contact information through text commands. A
task can have no clients, one client, or multiple clients.

## Adding a client

Use `client add` with a required `/name` field and any optional fields:

```text
client add /name Alice Tan /phone +65 9123 4567 /email alice@example.com /company Acme /preferred email
```

Available fields are `/name`, `/phone`, `/email`, `/address`, `/company`,
`/notes`, and `/preferred`. Preferred contact accepts `phone`, `email`, or
`other`.

Field values are not trimmed automatically. Leading or trailing whitespace is
rejected instead of silently removed.

| Field | Requirement |
| --- | --- |
| Name | Required; 1 to 100 letters or spaces; no numbers or punctuation |
| Phone | Optional; 3 to 32 characters; at least three digits; may contain spaces, `+`, `-`, `.`, `(`, and `)` |
| Email | Optional; 3 to 254 characters; exactly one `@`, text on both sides, and no whitespace |
| Address | Optional; at most 200 characters; no tabs or newlines |
| Company | Optional; at most 100 characters; no tabs or newlines |
| Notes | Optional; at most 2000 characters; newlines allowed; no tabs |
| Preferred contact | Optional; `phone`, `email`, or `other` |

Non-empty phone numbers and email addresses must be unique. Email comparison
is case-insensitive. Phone comparison ignores spaces, parentheses, periods,
and hyphens. Duplicate values are rejected without creating or changing a
client.

Use `\n` in notes to enter a newline and `\\` to enter a literal backslash.

## Listing and viewing clients

List all clients, sorted by name:

```text
client list
```

View one client and its assigned tasks:

```text
client view C001
```

Client IDs are generated automatically and are entered case-insensitively.

## Finding clients

Search client names using a case-insensitive partial match:

```text
client find alice
```

Only names are searched.

## Editing a client

Supply only the fields that should change:

```text
client edit C001 /phone +65 9876 5432 /preferred phone
```

Omitted fields remain unchanged. Clear optional fields with `/clear`:

```text
client edit C001 /clear phone /clear address
```

The client ID and name cannot be cleared.

## Associating clients with tasks

Associate one client with one or more task numbers:

```text
client associate C001 /task 1 /task 2
```

Dissociate a client from tasks:

```text
client dissociate C001 /task 1
```

A client cannot be associated with the same task twice. Ordinary dissociation
cannot remove a task's last client. Existing unassigned tasks remain valid.

Client IDs can also be supplied when creating tasks. The marker is optional and
may be repeated:

```text
todo Prepare invoice /client C001 /client C002
deadline Submit proposal /by 2026-09-30 /client C001
event Client meeting /from 2026-09-20 /to 2026-09-20 /client C001
```

## Deleting a client

First request deletion:

```text
client delete C001
```

Verity displays a warning without changing data. Confirm permanent deletion
with:

```text
client delete C001 confirm
```

Deleting a client never deletes tasks. Verity removes the client's active task
associations and adds a note such as:

```text
Former client Alice Tan (C001) was deleted.
```

## Data files

Tasks remain in `data/verity.txt`. Clients are stored separately in
`data/clients.txt`, which is created on the first client change. Existing task
files remain compatible.

The first line of `clients.txt` stores the next numeric ID. Each remaining line
stores a tab-separated client record in this order: record type, ID, name,
phone, email, address, company, notes, and preferred contact. For example:

```text
NEXT_ID	2
C	C001	Alice Tan	+65 9123 4567	alice@example.com		Acme	Called\nRequested quote	email
```

The next-ID value is always greater than every stored ID, so deleted IDs are
not reused.

Newly saved task lines append two tab-separated fields to the existing task
format: comma-separated active client IDs and escaped former-client notes.
Legacy task lines without these fields continue to load as unassigned tasks.
Once tasks are saved again, they use the extended format. A task file that
references a client missing from `clients.txt` is treated as corrupted data.

In client and task metadata, `\n` represents a newline and `\\` represents a
literal backslash.

If saved client data is corrupted, Verity reports the corrupted data and does
not process commands.
