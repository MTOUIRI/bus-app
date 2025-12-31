CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_phone ON users(phone);

-- Operators table
CREATE TABLE operators (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    description TEXT,
    rating DECIMAL(3,2),
    logo_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_operator_name ON operators(name);

-- Buses table
CREATE TABLE buses (
    id BIGSERIAL PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL UNIQUE,
    bus_type VARCHAR(50) NOT NULL,
    total_seats INTEGER NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year VARCHAR(4),
    has_wifi BOOLEAN NOT NULL DEFAULT FALSE,
    has_ac BOOLEAN NOT NULL DEFAULT FALSE,
    has_restroom BOOLEAN NOT NULL DEFAULT FALSE,
    operator_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_bus_operator FOREIGN KEY (operator_id) REFERENCES operators(id)
);

CREATE INDEX idx_bus_number ON buses(bus_number);

-- Routes table
CREATE TABLE routes (
    id BIGSERIAL PRIMARY KEY,
    origin VARCHAR(200) NOT NULL,
    destination VARCHAR(200) NOT NULL,
    distance_km INTEGER NOT NULL,
    estimated_duration_minutes INTEGER NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_origin_destination ON routes(origin, destination);

-- Trips table
CREATE TABLE trips (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL,
    bus_id BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    available_seats INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_trip_route FOREIGN KEY (route_id) REFERENCES routes(id),
    CONSTRAINT fk_trip_bus FOREIGN KEY (bus_id) REFERENCES buses(id),
    CONSTRAINT fk_trip_operator FOREIGN KEY (operator_id) REFERENCES operators(id)
);

CREATE INDEX idx_trip_route_date ON trips(route_id, departure_time);
CREATE INDEX idx_trip_status ON trips(status);

-- Bookings table
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    passenger_name VARCHAR(150) NOT NULL,
    passenger_email VARCHAR(150) NOT NULL,
    passenger_phone VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_trip FOREIGN KEY (trip_id) REFERENCES trips(id)
);

CREATE INDEX idx_booking_code ON bookings(booking_code);
CREATE INDEX idx_user_id ON bookings(user_id);
CREATE INDEX idx_trip_id ON bookings(trip_id);
CREATE INDEX idx_status ON bookings(status);

-- Seats table
CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    booking_id BIGINT,
    seat_number VARCHAR(10) NOT NULL,
    seat_type VARCHAR(50) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    is_window BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_seat_trip FOREIGN KEY (trip_id) REFERENCES trips(id),
    CONSTRAINT fk_seat_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT uk_trip_seat_number UNIQUE (trip_id, seat_number)
);

CREATE INDEX idx_trip_available ON seats(trip_id, is_available);

-- Payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    paid_at TIMESTAMP,
    gateway_response TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE INDEX idx_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payment_status ON payments(status);