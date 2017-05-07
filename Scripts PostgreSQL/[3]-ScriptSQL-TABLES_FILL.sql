INSERT INTO categoria ( nombre ) VALUES
	( 'sin categoria' ),
	( 'integrados' ),
	( 'audio' ),
	( 'video' ),
	( 'pilas' ),
	( 'componentes' ),
	( 'accesorios' ),
	( 'reparacion' ),
	( 'almacenamiento' ),
	( 'cables' );

INSERT INTO producto ( nombre, stock, stock_minimo, precio_neto, categoria, casillero ) VALUES
	( 'reloj de tiempo real ds1307', 30, 10, 1990, 'integrados', 'b5' ),
	( 'i2c eeprom - 256kbit', 50, 10, 940, 'integrados', 'b4' ),
	( 'ic decoder 5 addr 4 data 16-dip - mc145027p', 60, 10, 900, 'integrados', 'b3' ),
	( 'rs232 converter smd - max3232', 35, 10, 1160, 'integrados', 'b2' ),
	( 'timer 555 formato dip 8 pines', 40, 10, 360, 'integrados', 'b1' ),
	( 'audífonos philips shl3000br', 30,5, 9990, 'audio', 'a1' ),
	( 'audífonos panasonic hs34pp violeta', 20, 5, 8990, 'audio', 'a1' ),
	( 'parlante pioneer ts-f16354r', 10, 2, 39890, 'audio', 'a2' ),
	( 'parlantes sony xs-gte1620', 14, 2, 45990, 'audio', 'a3' ),
	( 'microlabcaja acustica 6022', 10, 2, 119990, 'audio', 'a4' ),
	( 'microsoft webcam hd-3000', 10, 2, 25990, 'video', 'e2' ),
	( 'logitech webcam 525 hd', 10, 2, 36990, 'video', 'e3' ),
	( 'logitechweb cam full hd', 10, 2, 56990, 'video', 'e1' ),
	( 'logitech webcam 425', 12, 2, 16990, 'video', 'e4' ),
	( 'microsoftwebcam hd-2300',10, 2, 14990, 'video', 'e5' ),
	( 'duracell pack 2 pilas aa', 80, 20, 1990, 'pilas', 'd1' ),
	( 'duracell pack 2 aaa 2', 70, 20, 1990, 'pilas', 'd1' ),
	( 'macrotel pack de pilas recargables aa', 20, 4, 6990, 'pilas', 'd1' ),
	( 'pack 2 pilas aaa sony', 40, 10, 1790, 'pilas', 'd1' ),
	( 'bateria duracell 9v', 50, 10, 2990, 'pilas', 'd1' ),
	( 'cable para protoboard café', 40, 5, 1850, 'cables', 'c7' ),
	( 'cable para protoboard gris', 39, 5, 1850, 'cables', 'c7' ),
	( 'condensador electrolítico 0,47 uf. 50v 5x11mm.', 30, 10, 1290, 'componentes', 'c6' ),
	( 'resistencia ntc 47d-15 ntc',40, 5, 1990, 'componentes', 'c5' ),
	( '3w 3 watt 5% metal oxide film resistor 1k ohm', 40, 5, 1490, 'componentes', 'c5' ),
	( 'genius teclado númerico numpad i110', 10, 2, 7990, 'accesorios', 'f1' ),
	( 'fiddler teclado bluetooth negro', 12, 2, 15990, 'accesorios', 'f1' ),
	( 'genius mouse mini pink', 15, 5, 14990, 'accesorios', 'f2' ),
	( 'microsoft mouse 1850 rojo', 15, 5, 9990, 'accesorios', 'f2' ),
	( 'logitech mouse usb alámbrico m105 blanco', 16, 5, 6990, 'accesorios', 'f2' ),
	( 'cautin electrico', 10, 2, 1990, 'reparacion', 'g1' ),
	( 'pasta para estaño', 20, 5, 3990, 'reparacion', 'g1' ),
	( 'soldadura barra', 140, 40, 390, 'reparacion', 'g1' ),
	( 'estaño xmetro', 80, 10, 480, 'reparacion', 'g1' ),
	( 'broca 3"', 30, 5, 990, 'reparacion', 'g2' ),
	( 'disco duro wd 1tb', 10, 3, 45990, 'almacenamiento', 'h1' ),
	( 'disco duro wd 750gb', 10, 3, 29990, 'almacenamiento', 'h1' ),
	( 'pendrive sandisk 8gb', 30, 5, 5990, 'almacenamiento', 'h1' ),
	( 'pendrive sandisk 16gb', 30, 5, 9990, 'almacenamiento', 'h1' ),
	( 'tarjeta memoria sandisk 16gb', 25, 5, 9990, 'almacenamiento', 'h1' ),
	( 'alargador electrico 5 salidas', 10, 2, 3990, 'cables', 'i1' ),
	( 'cable de red 15mts', 10, 2, 3990, 'cables', 'i1' ),
	( 'cable de red 20mts', 10, 2, 4990, 'cables', 'i1' ),
	( 'cable de red 10mts', 10, 2, 2990, 'cables', 'i1' ),
	( 'cable de red 5mts', 10, 2, 1990, 'cables', 'i1' ),
	( 'protoboard', 10, 2, 10990, 'sin categoria', 'z1' ),
	( 'led', 100, 20, 190, 'sin categoria', 'z2' ),
	( 'equipo iluminario', 10, 2, 49990, 'sin categoria', 'z3' ),
	( 'ampolleta 80w', 30, 10, 350, 'sin categoria', 'z4' ),
	( 'antena wifi', 10, 2, 4990, 'sin categoria', 'z5' ),
	( 'control pc joystick',10, 2, 7990, 'sin categoria', 'z6' ),
	( 'control wii u alternativo',10, 2, 7990, 'sin categoria', 'z6' ),
	( 'control ps3 original',10, 2, 23990, 'sin categoria', 'z6' ),
	( 'control xbox original', 10, 2, 26990, 'sin categoria', 'z6' ),
	( 'tarjetas de sonido para pc', 10, 2, 9900, 'sin categoria', 'z7' );

SELECT crear_boleta ( '174551479', '18489730k' );
SELECT crear_boleta ( '18813162k', '169151156' );
SELECT crear_boleta ( '224472730', '148736243' );
SELECT crear_boleta ( '212205583', '163640724' );
SELECT crear_boleta ( '235750511', '18813162k' );
SELECT crear_boleta ( '201714079', '164293866' );
SELECT crear_boleta ( '136310127', '161618004' );
SELECT crear_boleta ( '67057449k', '196216189' );
SELECT crear_boleta ( '198373877', '997163392' );
SELECT crear_boleta ( '227329769', '635504853' );
SELECT crear_boleta ( '123514173', '166659531' );
SELECT crear_boleta ( '152491107', '249273821' );
SELECT crear_boleta ( '248156260', '170316568' );
SELECT crear_boleta ( '15147456k', '171490766' );

SELECT agregar_venta ( 1, 1, 2 );
SELECT agregar_venta ( 1, 3, 2 );
SELECT agregar_venta ( 1, 4, 6 );
SELECT agregar_venta ( 1, 6, 2 );
SELECT agregar_venta ( 1, 2, 4 );
SELECT agregar_venta ( 2, 12, 6 );
SELECT agregar_venta ( 3, 13, 3 );
SELECT agregar_venta ( 4, 11, 1 );
SELECT agregar_venta ( 5, 7, 2 );
SELECT agregar_venta ( 6, 19, 5 );
SELECT agregar_venta ( 7, 14, 1 );
SELECT agregar_venta ( 8, 17, 2 );
SELECT agregar_venta ( 9, 12, 1 );