CREATE OR REPLACE FUNCTION crear_boleta ( run_cliente text, run_vendedor text )
RETURNS void AS $$
    BEGIN
		-- Se crea la Boleta con run cliente y run vendedor.
		INSERT INTO boleta ( run_cliente, run_vendedor )
			VALUES ( run_cliente, run_vendedor );
    END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION boleta_efectuada ( codigo_boleta bigint )
RETURNS boolean AS $$
    BEGIN
		-- Retorna TRUE si la boleta ha sido efectuada, FALSE si esta en curso.
		RETURN (
			SELECT efectuada FROM boleta
				WHERE codigo = codigo_boleta
		);
    END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION boleta_vacia ( codigo_boleta bigint )
RETURNS boolean AS $$
    BEGIN
		-- Retorna TRUE si la boleta no tiene ventas, FALSE en caso contrario.
		RETURN (
			SELECT total_neto FROM boleta
				WHERE codigo = codigo_boleta
		) = 0;
    END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION agregar_venta ( codigo_boleta bigint, codigo_producto bigint, cantidad integer )
RETURNS void AS $$
    DECLARE
		total_venta integer;
    BEGIN
		-- Se verifica que la boleta este en curso.
		IF boleta_efectuada ( codigo_boleta ) THEN
			RAISE EXCEPTION '[!] Boleta NO esta en curso.';	
		END IF;
		-- Se descuenta el stock del producto.
		UPDATE producto SET stock = stock - cantidad
			WHERE codigo = codigo_producto;
		-- Se genera la venta asociada a la boleta.
		INSERT INTO venta ( producto, boleta, cantidad )
			VALUES ( codigo_producto, codigo_boleta, cantidad );
		-- Se recupera el precio neto del producto.
		SELECT precio_neto FROM producto INTO total_venta
			WHERE codigo = codigo_producto;
		-- Se calcula el precio neto total de la venta del producto.
		total_venta := total_venta * cantidad;
		-- Se actualiza el total neto de la boleta.
		UPDATE boleta SET total_neto = total_neto + total_venta
			WHERE codigo = codigo_boleta;
    END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION cancelar_venta ( codigo_venta bigint, codigo_boleta bigint, codigo_producto bigint, cantidad integer )
RETURNS void AS $$
    DECLARE
		total_venta integer;
    BEGIN
		-- Se repone el stock del producto.
		UPDATE producto SET stock = stock + cantidad
			WHERE codigo = codigo_producto;
		-- Se recupera el precio neto del producto.
		SELECT precio_neto FROM producto INTO total_venta
			WHERE codigo = codigo_producto;
		-- Se calcula el precio neto total de la venta del producto.
		total_venta := total_venta * cantidad;
		-- Se actualiza el total neto de la boleta.
		UPDATE boleta SET total_neto = total_neto - total_venta
			WHERE codigo = codigo_boleta;
		-- Se elimina la venta.
		DELETE FROM venta
			WHERE codigo = codigo_venta;
    END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION cancelar_boleta ( codigo_boleta bigint )
RETURNS void AS $$
    DECLARE
		REC record;
    BEGIN
		-- Se verifica que la boleta este en curso.
		IF boleta_efectuada ( codigo_boleta ) THEN
			RAISE EXCEPTION '[!] Boleta NO esta en curso.';	
		END IF;
		-- Se cancelan todas las ventas asociadas.
		FOR REC IN ( SELECT * FROM venta ) LOOP
			IF REC.boleta = codigo_boleta THEN
				EXECUTE cancelar_venta ( REC.codigo, codigo_boleta, REC.producto, REC.cantidad );
			END IF;
		END LOOP;
		-- Se cancela la boleta.
		DELETE FROM boleta
			WHERE codigo = codigo_boleta;
    END;
$$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION stock_critico ( codigo_producto bigint )
RETURNS boolean AS $$
    BEGIN
		-- Se verifica el estado critico del stock.
		RETURN (
			SELECT stock - stock_minimo FROM producto
			WHERE codigo = codigo_producto
		) < 0;
    END;
$$ LANGUAGE PLPGSQL;

CREATE VIEW productos AS
	SELECT codigo, nombre, stock, stock_minimo, precio_neto, precio_neto * 1.19 AS precio_bruto, categoria, casillero FROM producto;
	
CREATE VIEW boletas AS
	SELECT codigo, fecha, total_neto, total_neto * 1.19 AS total_bruto, run_cliente, run_vendedor, efectuada FROM boleta;