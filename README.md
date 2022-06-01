# Discord-Graph-Bot

Discord bot that provides help with algorithms or graph related questions

## Usage

All commands return a human readable version of the `Topic` object (see diagram below), which contains:

- article title, *using a bold font*
- content, *plain text with html elements removed from the article body*
- id, *used to uniquely identify the article and provide a permanent link to it*

The cumulative length of the items above does not exceed 2000 characters (Discord's message limit for non-nitro accounts).

### Supported commands

Currently, the supported commands are the following:

- `/search [query]` searches Wikipedia for the user inputted query and replies to the message

## Setup

- Create your bot at [https://discord.com/developers/applications](https://discord.com/developers/applications)
- Copy its token from [https://discord.com/developers/applications/YOUR_BOT_ID/bot](https://discord.com/developers/applications/YOUR_BOT_ID/bot) into [example.config.env](src/resources/example.config.env)
- Rename [example.config.env](src/resources/example.config.env) to [config.env](src/resources/config.env)

## Class diagram

<a href="diagram/diagram.svg">
  <img src="diagram/diagram.svg" alt="Class diagram">
</a>
