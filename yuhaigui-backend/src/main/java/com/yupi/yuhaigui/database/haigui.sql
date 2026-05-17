CREATE TABLE users (
    uuid CHAR(36) NOT NULL DEFAULT UUID() PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chat_room (
    session_id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    room_id BIGINT NOT NULL,
    PRIMARY KEY (session_id),
    UNIQUE KEY unique_username_room (username, room_id),
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE chat_message (
    message_id INT NOT NULL AUTO_INCREMENT,
    session_id INT NOT NULL,
    message_order INT NOT NULL,
    type ENUM('SYSTEM', 'USER', 'AI') NOT NULL,
    message TEXT NOT NULL,
    PRIMARY KEY (message_id),
    FOREIGN KEY (session_id) REFERENCES chat_room(session_id) ON DELETE CASCADE ON UPDATE CASCADE
);