# Shopify2QBO - Export your Shopify orders to QuickBooks Online.

This is a Proof of Concept(PoC) application to export Shopify orders to QuickBooks Online.

Many QuickBooks Online (QBO) users in Australia use Shopify ECommerce system as part of running their business. However, the lack of any integration between Shopify and QBO means the data does not flow between the systems. This forces the small business to perform a lot of manual steps to reconcile these transactions back to QBO, taking time away from running their small business.

## Table of Contents

* [Requirements](#requirements)
* [Running the code](#running-the-code)

## Requirements

In order to successfully run this application, you need:

1. Java 1.8 or later.
2. Apache Maven installed on your computer. You can get Maven at [Installing Apache Maven] (https://maven.apache.org/install.html)
3. A [developer.intuit.com](http://developer.intuit.com) account.
4. A [sandbox QBO company] (https://developer.intuit.com/v2/ui#/sandbox).
5. A [developer Shopify] (https://accounts.shopify.com/) account.
6. A [Shopify development store] (https://www.shopify.com.au/partners).

## Running the code

1. Clone the GitHub repository to your computer.
2. cd to the project directory</li>
3. Run the command:`mvn spring-boot:run`
4. Wait until the terminal output displays the "Started PosToQboApplication in xxx seconds" message.
5. Your application should now be accessible at http://localhost:8080/ 
