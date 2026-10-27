# How to fix the current build error

## Problem
`ApplicationLoader.java` became truncated during automated edits (file is ~25 KB and the write tool has size limits).

## Exact fix (run these commands in your local repo)

```bash
cd /path/to/zoxgram

# Restore the complete original file from a known good commit
git checkout d83d4d2cb36dda6d864bf15316e714b3909e7b66 -- TMessagesProj/src/main/java/org/telegram/messenger/ApplicationLoader.java

# Verify it is complete (~724 lines)
wc -l TMessagesProj/src/main/java/org/telegram/messenger/ApplicationLoader.java

# Commit and push
git add TMessagesProj/src/main/java/org/telegram/messenger/ApplicationLoader.java
git commit -m "Restore full original ApplicationLoader.java - fix build"
git push
```

After this the project should compile.

## What is already safely in the repo
- Entire `zoxgram-features` module (all feature helpers, red theme, branding strings, settings activity)
- Module is currently **temporarily disabled** in `TMessagesProj/build.gradle` so the main project builds.

## After build is green
1. Uncomment the line in `TMessagesProj/build.gradle`:
   `implementation project(':zoxgram-features')`
2. Optionally add the 2-line Zoxgram init inside `ApplicationLoader.postInitApplication()`.
