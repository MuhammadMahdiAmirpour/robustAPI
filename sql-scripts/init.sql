DELIMITER //

CREATE PROCEDURE GetUserByNationalId(IN nationalId VARCHAR(255))
BEGIN
SELECT * FROM users WHERE national_id = nationalId;
END //

DELIMITER ;
