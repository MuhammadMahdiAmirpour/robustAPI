USE robustapidb;

DELIMITER
//

DROP PROCEDURE IF EXISTS GetUsersByPhoneNumber//

CREATE PROCEDURE GetUsersByPhoneNumber(IN phoneNumber VARCHAR (255))
BEGIN
SELECT *
FROM users
WHERE phone_number = phoneNumber;
END
//

DELIMITER ;

-- Verify the procedure was created
SELECT ROUTINE_NAME, ROUTINE_TYPE
FROM INFORMATION_SCHEMA.ROUTINES
WHERE ROUTINE_SCHEMA = 'robustapidb'
  AND ROUTINE_NAME = 'GetUsersByPhoneNumber';
