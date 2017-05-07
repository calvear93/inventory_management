CREATE TABLE categoria (
	nombre text NOT NULL,
	CONSTRAINT pk_categoria PRIMARY KEY ( nombre )
);
 
 CREATE TABLE producto (
	codigo bigserial NOT NULL,
	nombre text NOT NULL,
	descripcion text DEFAULT 'Sin descripcion',
	stock integer DEFAULT 1 CHECK ( stock >= 0 ),
	stock_minimo integer NOT NULL,
	precio_neto integer NOT NULL,
	categoria text NOT NULL,
	casillero text NOT NULL,
	CONSTRAINT pk_producto PRIMARY KEY ( codigo ),
	CONSTRAINT fk_categoria FOREIGN KEY ( categoria ) REFERENCES categoria( nombre ) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE boleta (
	codigo bigserial NOT NULL,
	fecha date DEFAULT current_date,
	total_neto integer DEFAULT 0,
	run_cliente text NOT NULL,
	run_vendedor text NOT NULL,
	efectuada boolean DEFAULT FALSE,
	CONSTRAINT pk_boleta PRIMARY KEY ( codigo )
);

CREATE TABLE venta (
	producto bigserial NOT NULL,
	boleta bigserial NOT NULL,
	cantidad integer DEFAULT 1 CHECK ( cantidad > 0 ),
	CONSTRAINT pk_venta PRIMARY KEY ( producto, boleta ),
	CONSTRAINT fk_producto FOREIGN KEY ( producto ) REFERENCES producto( codigo ) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT fk_boleta FOREIGN KEY ( boleta ) REFERENCES boleta( codigo ) ON DELETE RESTRICT ON UPDATE RESTRICT
);