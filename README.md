# Shopify2QBO - Export your Shopify orders to QuickBooks Online.

This is a Proof of Concept (PoC) application to export Shopify orders to QuickBooks Online.

Many QuickBooks Online (QBO) users in Australia use Shopify ECommerce system as part of running their business. However, the lack of any integration between Shopify and QBO means the data does not flow between the systems. This forces the small business to perform a lot of manual steps to reconcile these transactions back to QBO, taking time away from running their small business.

## Table of Contents

* [Requirements](#requirements)
* [Running the code](#running-the-code)
* [Notes](#notes)

## Requirements

In order to successfully run this application, you need:

1. Java 1.8 or later.
2. Apache Maven installed on your computer. You can get Maven at [Installing Apache Maven](https://maven.apache.org/install.html)
3. A [developer.intuit.com](http://developer.intuit.com) account.
4. A [sandbox QBO company](https://developer.intuit.com/v2/ui#/sandbox).
5. A [developer Shopify](https://accounts.shopify.com/) account.
6. A [Shopify development store](https://www.shopify.com.au/partners).

## Running the code

1. Clone the GitHub repository to your computer.
2. cd to the project directory</li>
3. Run the command:`mvn spring-boot:run`
4. Wait until the terminal output displays the `Started PosToQboApplication in xxx seconds` message.
5. Your application should now be accessible at http://localhost:8080/ 

## Notes

[1]
This application uses in-memory database - H2 - to store the customer mappings (between the Sopify customers and QBO customers). When you add a Shopify customer to QBO, an in-memory mapping is created. The next when you try to add the same customer, the application knows and reads the necessary customer details from in-memory database. Thus avoiding adding duplicate customers in QBO. However, if you restrated the application, the customer mapping is lost. After restarting, if you tried to add a customer that is already present in QBO, the application will fail to add the customer (as QBO does not allow duplicate customer display names).

[2]
Deep link to QBO (on results page) is not working (it is WIP).