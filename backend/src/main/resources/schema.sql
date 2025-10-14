DROP TABLE IF EXISTS reservation_item;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS ticket_option;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS customer;


CREATE TABLE customer (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(100) NOT NULL,
                          last_name VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          registration_date TIMESTAMP NOT NULL,
                          total_attendances INT DEFAULT 0,
                          current_streak INT DEFAULT 0,
                          loyalty_free BOOLEAN DEFAULT FALSE,
                          active BOOLEAN DEFAULT TRUE
);


CREATE TABLE event (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       description VARCHAR(1000),
                       type VARCHAR(50) NOT NULL,
                       start_date_time TIMESTAMP NOT NULL,
                       end_date_time TIMESTAMP NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       active BOOLEAN DEFAULT TRUE
);


CREATE TABLE ticket_option (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               event_id BIGINT NOT NULL,
                               name VARCHAR(100) NOT NULL,
                               price DECIMAL(10,2) NOT NULL,
                               capacity INT NOT NULL,
                               sold INT DEFAULT 0,
                               version INT DEFAULT 0,
                               CONSTRAINT fk_ticket_event FOREIGN KEY (event_id) REFERENCES event(id)
);



CREATE TABLE reservation (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             customer_id BIGINT NOT NULL,
                             event_id BIGINT NOT NULL,
                             status VARCHAR(50) NOT NULL,
                             attendee_name VARCHAR(200) NOT NULL,
                             attended_by VARCHAR(200),
                             created_by_admin BOOLEAN DEFAULT TRUE,
                             loyalty_free BOOLEAN DEFAULT FALSE,
                             total DECIMAL(10,2) DEFAULT 0.00,
                             created_at TIMESTAMP NOT NULL,
                             paid_at TIMESTAMP,
                             active BOOLEAN DEFAULT TRUE,
                             CONSTRAINT fk_reservation_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
                             CONSTRAINT fk_reservation_event FOREIGN KEY (event_id) REFERENCES event(id)
);


CREATE TABLE reservation_item (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  reservation_id BIGINT NOT NULL,
                                  ticket_option_id BIGINT NOT NULL,
                                  quantity INT NOT NULL,
                                  unit_price DECIMAL(10,2) NOT NULL,
                                  CONSTRAINT fk_item_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(id),
                                  CONSTRAINT fk_item_ticket FOREIGN KEY (ticket_option_id) REFERENCES ticket_option(id)
);
