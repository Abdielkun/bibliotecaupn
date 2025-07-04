-- Insertar libro
insert into libro (titulo, autor, isbn, editorial, disponible, version) values ('el principito', 'antoine de saint-exupéry', '978-0156012195','coquito', 1, 0);

-- Insertar usuario
insert into usuario (nombre, email, username, password, rol) values ('admin', 'admin@upn.edu.pe', 'adminn', '$2a$10$3Oc6EzbnHhR7tD7tUgUep.9r6mLPxzU8Aq63sq6VbW7H3MCXwhg0K', 'ROLE_ADMIN');

-- Insertar préstamo (asumiendo que libro.id = 1 y usuario.id = 1)
insert into prestamo (libro_id, usuario_id, fecha_prestamo, fecha_devolucion, renovado) values (1, 1, '2024-05-01', '2024-05-15', 0);
