create table customers (
  customerNumber INT NOT NULL,
  customerName varchar(50) NOT NULL,
  contactLastName varchar(50) NOT NULL,
  contactFirstName varchar(50) NOT NULL,
  phone varchar(50) NOT NULL,
  addressLine1 varchar(50) NOT NULL,
  addressLine2 varchar(50) DEFAULT NULL,
  city varchar(50) NOT NULL,
  state varchar(50) DEFAULT NULL,
  postalCode varchar(15) DEFAULT NULL,
  country varchar(50) NOT NULL,
  salesRepEmployeeNumber INT DEFAULT NULL,
  creditLimit INT DEFAULT NULL,
  PRIMARY KEY (customerNumber)
);


CREATE TABLE employees (
  employeeNumber INT NOT NULL,
  lastName varchar(50) NOT NULL,
  firstName varchar(50) NOT NULL,
  extension varchar(10) NOT NULL,
  email varchar(100) NOT NULL,
  officeCode varchar(10) NOT NULL,
  reportsTo INT DEFAULT NULL,
  jobTitle varchar(50) NOT NULL,
  PRIMARY KEY (employeeNumber)
);


CREATE TABLE offices (
  officeCode varchar(10) NOT NULL,
  city varchar(50) NOT NULL,
  phone varchar(50) NOT NULL,
  addressLine1 varchar(50) NOT NULL,
  addressLine2 varchar(50) DEFAULT NULL,
  state varchar(50) DEFAULT NULL,
  country varchar(50) NOT NULL,
  postalCode varchar(15) NOT NULL,
  territory varchar(10) NOT NULL,
  PRIMARY KEY (officeCode)
);  


CREATE TABLE orderdetails (
  orderNumber INT NOT NULL,
  productCode varchar(15) NOT NULL,
  quantityOrdered INT NOT NULL,
  priceEach text NOT NULL,
  orderLineNumber INT NOT NULL,
  PRIMARY KEY (orderNumber,productCode)
);


CREATE TABLE orders (
  orderNumber INT NOT NULL,
  orderDate date NOT NULL,
  requiredDate date NOT NULL,
  shippedDate date DEFAULT NULL,
  status varchar(15) NOT NULL,
  comments text,
  customerNumber INT NOT NULL,
  PRIMARY KEY (orderNumber)
);


CREATE TABLE payments (
  customerNumber INT NOT NULL,
  checkNumber varchar(50) NOT NULL,
  paymentDate date NOT NULL,
  amount INT  NOT NULL,
  PRIMARY KEY (customerNumber,checkNumber)
);


CREATE TABLE productlines (
  productLine varchar(50) NOT NULL,
  textDescription varchar(4000) DEFAULT NULL,
  htmlDescription text,
  image text,
  PRIMARY KEY (productLine)
);



CREATE TABLE products (
  productCode varchar(15) NOT NULL,
  productName varchar(70) NOT NULL,
  productLine varchar(50) NOT NULL,
  productScale varchar(10) NOT NULL,
  productVendor varchar(50) NOT NULL,
  productDescription text NOT NULL,
  quantityInStock INT NOT NULL,
  buyPrice varchar(15) NOT NULL,
  MSRP varchar(15) NOT NULL,
  PRIMARY KEY (productCode)
);