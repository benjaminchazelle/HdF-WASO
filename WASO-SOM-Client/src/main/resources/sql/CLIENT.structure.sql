CREATE TABLE CLIENT (
  ClientID int NOT NULL PRIMARY KEY,
  TypeClient varchar(1) NOT NULL,
  Denomination varchar(255) NOT NULL,
  Adresse varchar(255),
  Ville varchar(255)
)
