--
-- PostgreSQL database dump
--

-- Dumped from database version 16.8 (Ubuntu 16.8-1.pgdg24.04+1)
-- Dumped by pg_dump version 16.8 (Ubuntu 16.8-1.pgdg24.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: actualizar_stock(character varying, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.actualizar_stock(IN cod character varying, IN nuevo_stock integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = cod) THEN
        RAISE EXCEPTION 'No existe un producto con el código "%".', cod;
    END IF;

    IF nuevo_stock < 0 THEN
        RAISE EXCEPTION 'El stock no puede ser negativo.';
    END IF;

    UPDATE productos SET stock = nuevo_stock WHERE codigo = cod;
END;
$$;


ALTER PROCEDURE public.actualizar_stock(IN cod character varying, IN nuevo_stock integer) OWNER TO postgres;

--
-- Name: dar_de_baja_producto(character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.dar_de_baja_producto(IN cod character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = cod) THEN
        RAISE EXCEPTION 'El producto con código "%" no existe.', cod;
    END IF;

    UPDATE productos SET stock = 0 WHERE codigo = cod;
END;
$$;


ALTER PROCEDURE public.dar_de_baja_producto(IN cod character varying) OWNER TO postgres;

--
-- Name: listar_productos(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.listar_productos() RETURNS TABLE(codigo character varying, nombre character varying, descripcion text, precio double precision, stock integer, categoria text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.codigo,
        p.nombre,
        p.descripcion,
        p.precio,
        p.stock,
        c.nombre
    FROM productos p
    JOIN categorias c ON p.categoria_id = c.id;
END;
$$;


ALTER FUNCTION public.listar_productos() OWNER TO postgres;

--
-- Name: productos_bajo_minimo(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.productos_bajo_minimo() RETURNS TABLE(codigo character varying, nombre character varying, stock integer, stock_minimo integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT codigo, nombre, stock, stock_minimo
    FROM productos
    WHERE stock <= stock_minimo;
END;
$$;


ALTER FUNCTION public.productos_bajo_minimo() OWNER TO postgres;

--
-- Name: registrar_movimiento_stock(character varying, integer, text, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.registrar_movimiento_stock(IN cod_producto character varying, IN cantidad integer, IN motivo text, IN usuario_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO movimientos_stock (codigo_producto, cantidad, motivo, usuario_id)
    VALUES (cod_producto, cantidad, motivo, usuario_id);
END;
$$;


ALTER PROCEDURE public.registrar_movimiento_stock(IN cod_producto character varying, IN cantidad integer, IN motivo text, IN usuario_id integer) OWNER TO postgres;

--
-- Name: registrar_producto(character varying, character varying, text, double precision, integer, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.registrar_producto(IN cod character varying, IN nom character varying, IN desc_ text, IN prec double precision, IN cat_id integer, IN stock_ integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM categorias WHERE id = cat_id) THEN
        RAISE EXCEPTION 'La categoría con ID % no existe.', cat_id;
    END IF;

    IF EXISTS (SELECT 1 FROM productos WHERE codigo = cod) THEN
        RAISE EXCEPTION 'Ya existe un producto con el código "%".', cod;
    END IF;

    INSERT INTO productos (codigo, nombre, descripcion, precio, stock, categoria_id)
    VALUES (cod, nom, desc_, prec, stock_, cat_id);
END;
$$;


ALTER PROCEDURE public.registrar_producto(IN cod character varying, IN nom character varying, IN desc_ text, IN prec double precision, IN cat_id integer, IN stock_ integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: categorias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categorias (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion text
);


ALTER TABLE public.categorias OWNER TO postgres;

--
-- Name: categorias_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categorias_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categorias_id_seq OWNER TO postgres;

--
-- Name: categorias_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categorias_id_seq OWNED BY public.categorias.id;


--
-- Name: movimientos_stock; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.movimientos_stock (
    id integer NOT NULL,
    codigo_producto character varying(50),
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    cantidad integer NOT NULL,
    motivo text,
    usuario_id integer
);


ALTER TABLE public.movimientos_stock OWNER TO postgres;

--
-- Name: movimientos_stock_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.movimientos_stock_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.movimientos_stock_id_seq OWNER TO postgres;

--
-- Name: movimientos_stock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.movimientos_stock_id_seq OWNED BY public.movimientos_stock.id;


--
-- Name: productos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.productos (
    codigo character varying(50) NOT NULL,
    nombre character varying(150) NOT NULL,
    descripcion text,
    precio double precision NOT NULL,
    stock integer NOT NULL,
    categoria_id integer NOT NULL,
    stock_minimo integer DEFAULT 5,
    CONSTRAINT productos_stock_check CHECK ((stock >= 0))
);


ALTER TABLE public.productos OWNER TO postgres;

--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    correo character varying(150) NOT NULL,
    "contraseña" character varying(100) NOT NULL,
    rol character varying(50) DEFAULT 'usuario'::character varying,
    fecha_registro timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuarios_id_seq OWNER TO postgres;

--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- Name: categorias id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias ALTER COLUMN id SET DEFAULT nextval('public.categorias_id_seq'::regclass);


--
-- Name: movimientos_stock id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimientos_stock ALTER COLUMN id SET DEFAULT nextval('public.movimientos_stock_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- Data for Name: categorias; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categorias (id, nombre, descripcion) FROM stdin;
1	Tecnología	Productos electrónicos y dispositivos tecnológicos
2	Categoría 2	Descripción de categoría 2
3	Categoría 3	Descripción de categoría 3
4	Categoría 4	Descripción de categoría 4
5	Categoría 5	Descripción de categoría 5
6	Categoría 6	Descripción de categoría 6
7	Categoría 7	Descripción de categoría 7
8	Categoría 8	Descripción de categoría 8
9	Categoría 9	Descripción de categoría 9
10	Categoría 10	Descripción de categoría 10
11	Categoría 11	Descripción de categoría 11
12	Categoría 12	Descripción de categoría 12
13	Categoría 13	Descripción de categoría 13
14	Categoría 14	Descripción de categoría 14
15	Categoría 15	Descripción de categoría 15
16	Categoría 16	Descripción de categoría 16
17	Categoría 17	Descripción de categoría 17
18	Categoría 18	Descripción de categoría 18
19	Categoría 19	Descripción de categoría 19
20	Categoría 20	Descripción de categoría 20
\.


--
-- Data for Name: movimientos_stock; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.movimientos_stock (id, codigo_producto, fecha, cantidad, motivo, usuario_id) FROM stdin;
1	P108111	2025-05-07 14:30:48.379739	30	Reposición	1
2	P108111	2025-05-07 14:40:06.433965	30	Reposición	1
3	P108111	2025-05-07 14:40:10.822048	30	Reposición	1
4	P108111	2025-05-07 14:40:25.010794	30	Reposición	1
5	P108111	2025-05-07 14:40:27.176881	30	Reposición	1
6	P108111	2025-05-07 14:40:29.854132	30	Reposición	1
7	P108111	2025-05-07 14:40:31.405907	30	Reposición	1
8	P108111	2025-05-07 17:59:16.652655	30	Reposición	1
9	P108111	2025-05-07 17:59:27.438281	30	Reposición	1
10	P108111	2025-05-07 18:01:54.779192	30	Reposición	1
11	P108111	2025-05-08 10:25:28.218954	30	Reposición	1
12	P108111	2025-05-08 10:39:03.942899	30	Reposición	1
13	P108111	2025-05-08 10:43:27.793488	30	Reposición	1
14	P108111	2025-05-08 10:43:48.813181	30	Reposición	1
15	P108111	2025-05-08 10:44:14.7537	30	Reposición	1
16	1	2025-05-08 11:31:05.199233	22	Material residual	1
\.


--
-- Data for Name: productos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.productos (codigo, nombre, descripcion, precio, stock, categoria_id, stock_minimo) FROM stdin;
P107	Monitor	Monitor 27 pulgadas	850	25	1	5
P108	Monitor	Monitor 27 pulgadas	850	25	1	5
P10811	Monitor	Monitor 27 pulgadas	850	25	1	5
P11	Monitor	Monitor 27 pulgadas	850	25	1	5
P108111	Monitor	Monitor 27 pulgadas	850	30	1	5
P1	Monitor	Monitor 27 pulgadas	850	0	1	5
12	1	1	1	1	1	5
1	1	11	1	0	1	5
22	11	1	1	1	1	5
13232	1	1	1	1	1	5
1323211	1	1	1	1	1	5
122323	dad	sasaa	10000	1	1	5
1a	1	1	1	1	1	5
P0001	Producto 1	Este es el producto número 1.	234.9	55	7	5
P0002	Producto 2	Este es el producto número 2.	308.61	116	13	5
P0003	Producto 3	Este es el producto número 3.	347.06	20	13	5
P0004	Producto 4	Este es el producto número 4.	63.68	156	14	5
P0005	Producto 5	Este es el producto número 5.	44.05	141	7	5
P0006	Producto 6	Este es el producto número 6.	104.51	48	5	5
P0007	Producto 7	Este es el producto número 7.	82.97	107	10	5
P0008	Producto 8	Este es el producto número 8.	105.39	183	4	5
P0009	Producto 9	Este es el producto número 9.	255.75	76	17	5
P0010	Producto 10	Este es el producto número 10.	433.73	28	20	5
P0011	Producto 11	Este es el producto número 11.	357.53	52	9	5
P0012	Producto 12	Este es el producto número 12.	267.73	106	17	5
P0013	Producto 13	Este es el producto número 13.	408.94	64	7	5
P0014	Producto 14	Este es el producto número 14.	288.52	34	7	5
P0015	Producto 15	Este es el producto número 15.	170.28	93	5	5
P0016	Producto 16	Este es el producto número 16.	249.97	100	4	5
P0017	Producto 17	Este es el producto número 17.	216.84	4	15	5
P0018	Producto 18	Este es el producto número 18.	22.9	103	8	5
P0019	Producto 19	Este es el producto número 19.	444.79	111	14	5
P0020	Producto 20	Este es el producto número 20.	53.85	188	6	5
P0021	Producto 21	Este es el producto número 21.	230.07	118	14	5
P0022	Producto 22	Este es el producto número 22.	274.4	76	20	5
P0023	Producto 23	Este es el producto número 23.	282.67	111	18	5
P0024	Producto 24	Este es el producto número 24.	311.95	190	6	5
P0025	Producto 25	Este es el producto número 25.	457.72	133	10	5
P0026	Producto 26	Este es el producto número 26.	158.7	47	18	5
P0027	Producto 27	Este es el producto número 27.	153.88	23	10	5
P0028	Producto 28	Este es el producto número 28.	52.57	55	20	5
P0029	Producto 29	Este es el producto número 29.	258.65	50	5	5
P0030	Producto 30	Este es el producto número 30.	343.4	37	18	5
P0031	Producto 31	Este es el producto número 31.	304.12	104	17	5
P0032	Producto 32	Este es el producto número 32.	36.69	169	19	5
P0033	Producto 33	Este es el producto número 33.	95.17	86	17	5
P0034	Producto 34	Este es el producto número 34.	442.23	136	15	5
P0035	Producto 35	Este es el producto número 35.	68.14	156	20	5
P0036	Producto 36	Este es el producto número 36.	210.06	68	10	5
P0037	Producto 37	Este es el producto número 37.	406.66	45	19	5
P0038	Producto 38	Este es el producto número 38.	54.24	47	10	5
P0039	Producto 39	Este es el producto número 39.	100.77	114	19	5
P0040	Producto 40	Este es el producto número 40.	101.81	177	11	5
P0041	Producto 41	Este es el producto número 41.	419.39	171	18	5
P0042	Producto 42	Este es el producto número 42.	481.65	91	3	5
P0043	Producto 43	Este es el producto número 43.	272.99	112	18	5
P0044	Producto 44	Este es el producto número 44.	225.63	11	12	5
P0045	Producto 45	Este es el producto número 45.	66.46	198	4	5
P0046	Producto 46	Este es el producto número 46.	389.03	8	15	5
P0047	Producto 47	Este es el producto número 47.	95.66	69	11	5
P0048	Producto 48	Este es el producto número 48.	132.06	94	18	5
P0049	Producto 49	Este es el producto número 49.	160.65	61	2	5
P0050	Producto 50	Este es el producto número 50.	410.53	103	10	5
P0051	Producto 51	Este es el producto número 51.	469.56	125	10	5
P0052	Producto 52	Este es el producto número 52.	272.06	59	1	5
P0053	Producto 53	Este es el producto número 53.	169.44	81	6	5
P0054	Producto 54	Este es el producto número 54.	461.51	151	4	5
P0055	Producto 55	Este es el producto número 55.	12.95	115	11	5
P0056	Producto 56	Este es el producto número 56.	223.39	33	11	5
P0057	Producto 57	Este es el producto número 57.	244.29	61	16	5
P0058	Producto 58	Este es el producto número 58.	243.3	180	20	5
P0059	Producto 59	Este es el producto número 59.	232.27	10	15	5
P0060	Producto 60	Este es el producto número 60.	123.68	43	20	5
P0061	Producto 61	Este es el producto número 61.	339.92	187	17	5
P0062	Producto 62	Este es el producto número 62.	466.24	147	9	5
P0063	Producto 63	Este es el producto número 63.	229.15	179	17	5
P0064	Producto 64	Este es el producto número 64.	28.2	92	4	5
P0065	Producto 65	Este es el producto número 65.	128.45	170	3	5
P0066	Producto 66	Este es el producto número 66.	260.63	72	12	5
P0067	Producto 67	Este es el producto número 67.	104.86	104	6	5
P0068	Producto 68	Este es el producto número 68.	403.64	158	18	5
P0069	Producto 69	Este es el producto número 69.	341.29	114	14	5
P0070	Producto 70	Este es el producto número 70.	290.83	15	4	5
P0071	Producto 71	Este es el producto número 71.	209.36	184	14	5
P0072	Producto 72	Este es el producto número 72.	468.26	97	16	5
P0073	Producto 73	Este es el producto número 73.	262.05	29	10	5
P0074	Producto 74	Este es el producto número 74.	115.41	39	5	5
P0075	Producto 75	Este es el producto número 75.	32.68	133	13	5
P0076	Producto 76	Este es el producto número 76.	475.2	171	19	5
P0077	Producto 77	Este es el producto número 77.	121.81	100	9	5
P0078	Producto 78	Este es el producto número 78.	478.05	45	10	5
P0079	Producto 79	Este es el producto número 79.	394.37	61	10	5
P0080	Producto 80	Este es el producto número 80.	254.78	74	12	5
P0081	Producto 81	Este es el producto número 81.	247.62	46	11	5
P0082	Producto 82	Este es el producto número 82.	254.49	139	13	5
P0083	Producto 83	Este es el producto número 83.	376.05	98	7	5
P0084	Producto 84	Este es el producto número 84.	230.98	135	9	5
P0085	Producto 85	Este es el producto número 85.	249.7	175	14	5
P0086	Producto 86	Este es el producto número 86.	60.6	199	13	5
P0087	Producto 87	Este es el producto número 87.	373.61	100	20	5
P0088	Producto 88	Este es el producto número 88.	289.72	80	7	5
P0089	Producto 89	Este es el producto número 89.	496.62	178	5	5
P0090	Producto 90	Este es el producto número 90.	32.83	59	1	5
P0091	Producto 91	Este es el producto número 91.	394.1	38	13	5
P0092	Producto 92	Este es el producto número 92.	400.15	97	15	5
P0093	Producto 93	Este es el producto número 93.	341.31	136	10	5
P0094	Producto 94	Este es el producto número 94.	90.41	97	1	5
P0095	Producto 95	Este es el producto número 95.	282.8	180	14	5
P0096	Producto 96	Este es el producto número 96.	151.7	172	16	5
P0097	Producto 97	Este es el producto número 97.	76.88	178	17	5
P0098	Producto 98	Este es el producto número 98.	134	112	8	5
P0099	Producto 99	Este es el producto número 99.	200.59	127	13	5
P0100	Producto 100	Este es el producto número 100.	401.68	20	18	5
P0101	Producto 101	Este es el producto número 101.	66.44	193	8	5
P0102	Producto 102	Este es el producto número 102.	48.57	197	10	5
P0103	Producto 103	Este es el producto número 103.	216.72	40	10	5
P0104	Producto 104	Este es el producto número 104.	13.66	120	2	5
P0105	Producto 105	Este es el producto número 105.	130.42	192	2	5
P0106	Producto 106	Este es el producto número 106.	464.27	192	10	5
P0107	Producto 107	Este es el producto número 107.	149.82	184	3	5
P0108	Producto 108	Este es el producto número 108.	61.02	139	11	5
P0109	Producto 109	Este es el producto número 109.	294.17	59	10	5
P0110	Producto 110	Este es el producto número 110.	11.76	51	4	5
P0111	Producto 111	Este es el producto número 111.	129.66	41	16	5
P0112	Producto 112	Este es el producto número 112.	104.8	80	13	5
P0113	Producto 113	Este es el producto número 113.	10.65	103	8	5
P0114	Producto 114	Este es el producto número 114.	317.55	122	17	5
P0115	Producto 115	Este es el producto número 115.	402.77	132	17	5
P0116	Producto 116	Este es el producto número 116.	150.24	68	13	5
P0117	Producto 117	Este es el producto número 117.	448.73	80	1	5
P0118	Producto 118	Este es el producto número 118.	328.9	29	18	5
P0119	Producto 119	Este es el producto número 119.	459.81	55	2	5
P0120	Producto 120	Este es el producto número 120.	88.43	179	2	5
P0121	Producto 121	Este es el producto número 121.	303.46	174	7	5
P0122	Producto 122	Este es el producto número 122.	215.74	72	20	5
P0123	Producto 123	Este es el producto número 123.	87.37	58	5	5
P0124	Producto 124	Este es el producto número 124.	115.44	45	16	5
P0125	Producto 125	Este es el producto número 125.	243.77	135	16	5
P0126	Producto 126	Este es el producto número 126.	135.18	55	16	5
P0127	Producto 127	Este es el producto número 127.	20.3	79	2	5
P0128	Producto 128	Este es el producto número 128.	74.76	36	4	5
P0129	Producto 129	Este es el producto número 129.	210.46	144	14	5
P0130	Producto 130	Este es el producto número 130.	283.51	68	2	5
P0131	Producto 131	Este es el producto número 131.	44.96	155	10	5
P0132	Producto 132	Este es el producto número 132.	117.48	19	10	5
P0133	Producto 133	Este es el producto número 133.	257.92	153	15	5
P0134	Producto 134	Este es el producto número 134.	42.89	23	19	5
P0135	Producto 135	Este es el producto número 135.	54.23	183	7	5
P0136	Producto 136	Este es el producto número 136.	228.63	98	3	5
P0137	Producto 137	Este es el producto número 137.	335.07	20	1	5
P0138	Producto 138	Este es el producto número 138.	411.62	197	9	5
P0139	Producto 139	Este es el producto número 139.	39.12	112	12	5
P0140	Producto 140	Este es el producto número 140.	251.1	144	2	5
P0141	Producto 141	Este es el producto número 141.	181.66	96	9	5
P0142	Producto 142	Este es el producto número 142.	489.2	183	15	5
P0143	Producto 143	Este es el producto número 143.	259.6	85	12	5
P0144	Producto 144	Este es el producto número 144.	227.23	151	1	5
P0145	Producto 145	Este es el producto número 145.	209.68	109	19	5
P0146	Producto 146	Este es el producto número 146.	113.97	7	15	5
P0147	Producto 147	Este es el producto número 147.	461.04	150	2	5
P0148	Producto 148	Este es el producto número 148.	416.49	30	4	5
P0149	Producto 149	Este es el producto número 149.	124.69	141	7	5
P0150	Producto 150	Este es el producto número 150.	193.5	141	6	5
P0151	Producto 151	Este es el producto número 151.	404.86	176	4	5
P0152	Producto 152	Este es el producto número 152.	271.91	86	9	5
P0153	Producto 153	Este es el producto número 153.	477.34	51	3	5
P0154	Producto 154	Este es el producto número 154.	158.09	165	5	5
P0155	Producto 155	Este es el producto número 155.	359.71	49	17	5
P0156	Producto 156	Este es el producto número 156.	220.46	79	12	5
P0157	Producto 157	Este es el producto número 157.	142.71	130	1	5
P0158	Producto 158	Este es el producto número 158.	24.02	174	4	5
P0159	Producto 159	Este es el producto número 159.	431.01	138	7	5
P0160	Producto 160	Este es el producto número 160.	457.48	140	14	5
P0161	Producto 161	Este es el producto número 161.	463.39	154	5	5
P0162	Producto 162	Este es el producto número 162.	111.04	195	8	5
P0163	Producto 163	Este es el producto número 163.	39.01	125	16	5
P0164	Producto 164	Este es el producto número 164.	484.51	25	11	5
P0165	Producto 165	Este es el producto número 165.	478.13	174	12	5
P0166	Producto 166	Este es el producto número 166.	119.66	127	2	5
P0167	Producto 167	Este es el producto número 167.	354.79	122	1	5
P0168	Producto 168	Este es el producto número 168.	241.68	59	14	5
P0169	Producto 169	Este es el producto número 169.	260.37	195	15	5
P0170	Producto 170	Este es el producto número 170.	439.22	110	15	5
P0171	Producto 171	Este es el producto número 171.	437.28	74	17	5
P0172	Producto 172	Este es el producto número 172.	344.1	77	14	5
P0173	Producto 173	Este es el producto número 173.	265.76	5	8	5
P0174	Producto 174	Este es el producto número 174.	161.5	30	6	5
P0175	Producto 175	Este es el producto número 175.	236.15	59	9	5
P0176	Producto 176	Este es el producto número 176.	278.16	37	3	5
P0177	Producto 177	Este es el producto número 177.	185.12	104	17	5
P0178	Producto 178	Este es el producto número 178.	204.9	154	2	5
P0179	Producto 179	Este es el producto número 179.	250.53	167	3	5
P0180	Producto 180	Este es el producto número 180.	231.5	130	10	5
P0181	Producto 181	Este es el producto número 181.	175.02	125	17	5
P0182	Producto 182	Este es el producto número 182.	317.14	64	15	5
P0183	Producto 183	Este es el producto número 183.	62.72	59	13	5
P0184	Producto 184	Este es el producto número 184.	238.39	179	20	5
P0185	Producto 185	Este es el producto número 185.	274.72	114	5	5
P0186	Producto 186	Este es el producto número 186.	182.08	57	8	5
P0187	Producto 187	Este es el producto número 187.	11.73	148	20	5
P0188	Producto 188	Este es el producto número 188.	119.56	78	4	5
P0189	Producto 189	Este es el producto número 189.	250.26	193	7	5
P0190	Producto 190	Este es el producto número 190.	389.38	3	7	5
P0191	Producto 191	Este es el producto número 191.	287.96	197	11	5
P0192	Producto 192	Este es el producto número 192.	406.43	199	6	5
P0193	Producto 193	Este es el producto número 193.	334.21	67	17	5
P0194	Producto 194	Este es el producto número 194.	252.31	53	12	5
P0195	Producto 195	Este es el producto número 195.	489.72	80	6	5
P0196	Producto 196	Este es el producto número 196.	390.8	56	2	5
P0197	Producto 197	Este es el producto número 197.	439.24	29	4	5
P0198	Producto 198	Este es el producto número 198.	71.37	141	6	5
P0199	Producto 199	Este es el producto número 199.	19.5	130	17	5
P0200	Producto 200	Este es el producto número 200.	33.32	52	20	5
P0201	Producto 201	Este es el producto número 201.	157.45	55	16	5
P0202	Producto 202	Este es el producto número 202.	47.56	19	16	5
P0203	Producto 203	Este es el producto número 203.	243.66	28	14	5
P0204	Producto 204	Este es el producto número 204.	447.09	66	19	5
P0205	Producto 205	Este es el producto número 205.	389.03	49	19	5
P0206	Producto 206	Este es el producto número 206.	139.76	10	18	5
P0207	Producto 207	Este es el producto número 207.	252.08	125	19	5
P0208	Producto 208	Este es el producto número 208.	494.62	48	1	5
P0209	Producto 209	Este es el producto número 209.	271.53	58	3	5
P0210	Producto 210	Este es el producto número 210.	210.05	187	9	5
P0211	Producto 211	Este es el producto número 211.	152.87	68	9	5
P0212	Producto 212	Este es el producto número 212.	199.97	182	4	5
P0213	Producto 213	Este es el producto número 213.	211.62	59	7	5
P0214	Producto 214	Este es el producto número 214.	498.66	7	12	5
P0215	Producto 215	Este es el producto número 215.	480.57	182	4	5
P0216	Producto 216	Este es el producto número 216.	197.78	184	3	5
P0217	Producto 217	Este es el producto número 217.	480.38	90	2	5
P0218	Producto 218	Este es el producto número 218.	248.02	163	11	5
P0219	Producto 219	Este es el producto número 219.	442.41	108	9	5
P0220	Producto 220	Este es el producto número 220.	66.11	47	8	5
P0221	Producto 221	Este es el producto número 221.	127.78	63	8	5
P0222	Producto 222	Este es el producto número 222.	174.89	2	7	5
P0223	Producto 223	Este es el producto número 223.	488.78	172	13	5
P0224	Producto 224	Este es el producto número 224.	376.71	193	18	5
P0225	Producto 225	Este es el producto número 225.	278.4	165	10	5
P0226	Producto 226	Este es el producto número 226.	251.45	1	13	5
P0227	Producto 227	Este es el producto número 227.	11.76	57	4	5
P0228	Producto 228	Este es el producto número 228.	219.8	52	20	5
P0229	Producto 229	Este es el producto número 229.	462.23	129	2	5
P0230	Producto 230	Este es el producto número 230.	39.8	153	8	5
P0231	Producto 231	Este es el producto número 231.	330.89	158	15	5
P0232	Producto 232	Este es el producto número 232.	317.96	63	1	5
P0233	Producto 233	Este es el producto número 233.	240.79	115	18	5
P0234	Producto 234	Este es el producto número 234.	68.2	92	14	5
P0235	Producto 235	Este es el producto número 235.	262.7	157	10	5
P0236	Producto 236	Este es el producto número 236.	236.28	51	3	5
P0237	Producto 237	Este es el producto número 237.	279.99	0	12	5
P0238	Producto 238	Este es el producto número 238.	426.36	55	17	5
P0239	Producto 239	Este es el producto número 239.	161.36	170	3	5
P0240	Producto 240	Este es el producto número 240.	83.43	127	13	5
P0241	Producto 241	Este es el producto número 241.	265.59	134	1	5
P0242	Producto 242	Este es el producto número 242.	142.86	184	3	5
P0243	Producto 243	Este es el producto número 243.	277.39	155	14	5
P0244	Producto 244	Este es el producto número 244.	316.37	113	10	5
P0245	Producto 245	Este es el producto número 245.	461.87	119	20	5
P0246	Producto 246	Este es el producto número 246.	152.85	60	2	5
P0247	Producto 247	Este es el producto número 247.	112.54	81	12	5
P0248	Producto 248	Este es el producto número 248.	292.42	18	13	5
P0249	Producto 249	Este es el producto número 249.	233.99	129	1	5
P0250	Producto 250	Este es el producto número 250.	115.94	101	2	5
P0251	Producto 251	Este es el producto número 251.	261.03	147	18	5
P0252	Producto 252	Este es el producto número 252.	32.01	100	7	5
P0253	Producto 253	Este es el producto número 253.	234.88	173	13	5
P0254	Producto 254	Este es el producto número 254.	381.84	73	4	5
P0255	Producto 255	Este es el producto número 255.	73	102	3	5
P0256	Producto 256	Este es el producto número 256.	168.14	129	6	5
P0257	Producto 257	Este es el producto número 257.	374.53	3	6	5
P0258	Producto 258	Este es el producto número 258.	226.42	120	15	5
P0259	Producto 259	Este es el producto número 259.	201.3	193	13	5
P0260	Producto 260	Este es el producto número 260.	227.92	187	14	5
P0261	Producto 261	Este es el producto número 261.	253.81	107	10	5
P0262	Producto 262	Este es el producto número 262.	445.29	139	7	5
P0263	Producto 263	Este es el producto número 263.	236.32	161	7	5
P0264	Producto 264	Este es el producto número 264.	483.17	54	18	5
P0265	Producto 265	Este es el producto número 265.	32.71	14	3	5
P0266	Producto 266	Este es el producto número 266.	117.26	188	20	5
P0267	Producto 267	Este es el producto número 267.	470.5	6	17	5
P0268	Producto 268	Este es el producto número 268.	481.94	183	20	5
P0269	Producto 269	Este es el producto número 269.	477.12	73	2	5
P0270	Producto 270	Este es el producto número 270.	343.04	179	11	5
P0271	Producto 271	Este es el producto número 271.	412.31	143	12	5
P0272	Producto 272	Este es el producto número 272.	386.42	141	17	5
P0273	Producto 273	Este es el producto número 273.	98.92	2	14	5
P0274	Producto 274	Este es el producto número 274.	422.24	55	19	5
P0275	Producto 275	Este es el producto número 275.	309.26	161	11	5
P0276	Producto 276	Este es el producto número 276.	246.08	8	4	5
P0277	Producto 277	Este es el producto número 277.	290.71	132	13	5
P0278	Producto 278	Este es el producto número 278.	386.09	62	7	5
P0279	Producto 279	Este es el producto número 279.	397.42	145	9	5
P0280	Producto 280	Este es el producto número 280.	360.7	32	1	5
P0281	Producto 281	Este es el producto número 281.	475.99	167	3	5
P0282	Producto 282	Este es el producto número 282.	490.58	4	5	5
P0283	Producto 283	Este es el producto número 283.	48.93	175	1	5
P0284	Producto 284	Este es el producto número 284.	232.56	9	3	5
P0285	Producto 285	Este es el producto número 285.	301.18	96	7	5
P0286	Producto 286	Este es el producto número 286.	218.2	96	14	5
P0287	Producto 287	Este es el producto número 287.	357.51	178	6	5
P0288	Producto 288	Este es el producto número 288.	381.07	154	19	5
P0289	Producto 289	Este es el producto número 289.	474.77	66	15	5
P0290	Producto 290	Este es el producto número 290.	470.65	145	17	5
P0291	Producto 291	Este es el producto número 291.	368.33	42	12	5
P0292	Producto 292	Este es el producto número 292.	318.93	35	1	5
P0293	Producto 293	Este es el producto número 293.	491.61	85	19	5
P0294	Producto 294	Este es el producto número 294.	437.19	71	20	5
P0295	Producto 295	Este es el producto número 295.	26.6	47	8	5
P0296	Producto 296	Este es el producto número 296.	54.97	82	7	5
P0297	Producto 297	Este es el producto número 297.	215.62	194	20	5
P0298	Producto 298	Este es el producto número 298.	310.23	91	9	5
P0299	Producto 299	Este es el producto número 299.	160.93	157	10	5
P0300	Producto 300	Este es el producto número 300.	233.93	108	6	5
P0301	Producto 301	Este es el producto número 301.	65.85	11	13	5
P0302	Producto 302	Este es el producto número 302.	217.87	88	11	5
P0303	Producto 303	Este es el producto número 303.	61.88	69	2	5
P0304	Producto 304	Este es el producto número 304.	444.74	82	1	5
P0305	Producto 305	Este es el producto número 305.	454	52	20	5
P0306	Producto 306	Este es el producto número 306.	389.8	88	10	5
P0307	Producto 307	Este es el producto número 307.	16.09	67	11	5
P0308	Producto 308	Este es el producto número 308.	160.59	158	12	5
P0309	Producto 309	Este es el producto número 309.	106.38	165	16	5
P0310	Producto 310	Este es el producto número 310.	70.3	27	11	5
P0311	Producto 311	Este es el producto número 311.	342.73	83	3	5
P0312	Producto 312	Este es el producto número 312.	113.51	191	7	5
P0313	Producto 313	Este es el producto número 313.	202.19	150	7	5
P0314	Producto 314	Este es el producto número 314.	365.54	104	6	5
P0315	Producto 315	Este es el producto número 315.	453.91	184	16	5
P0316	Producto 316	Este es el producto número 316.	492.01	70	20	5
P0317	Producto 317	Este es el producto número 317.	288.03	179	8	5
P0318	Producto 318	Este es el producto número 318.	225.93	154	17	5
P0319	Producto 319	Este es el producto número 319.	330.42	56	13	5
P0320	Producto 320	Este es el producto número 320.	358.4	140	7	5
P0321	Producto 321	Este es el producto número 321.	409.12	103	17	5
P0322	Producto 322	Este es el producto número 322.	465.09	115	14	5
P0323	Producto 323	Este es el producto número 323.	293.78	59	7	5
P0324	Producto 324	Este es el producto número 324.	173.38	154	2	5
P0325	Producto 325	Este es el producto número 325.	253.54	58	5	5
P0326	Producto 326	Este es el producto número 326.	408.74	159	5	5
P0327	Producto 327	Este es el producto número 327.	403.51	188	4	5
P0328	Producto 328	Este es el producto número 328.	273.37	120	16	5
P0329	Producto 329	Este es el producto número 329.	254.6	178	8	5
P0330	Producto 330	Este es el producto número 330.	357.3	58	15	5
P0331	Producto 331	Este es el producto número 331.	145.06	63	8	5
P0332	Producto 332	Este es el producto número 332.	389.02	83	8	5
P0333	Producto 333	Este es el producto número 333.	387.5	41	11	5
P0334	Producto 334	Este es el producto número 334.	325.67	134	15	5
P0335	Producto 335	Este es el producto número 335.	421.42	127	6	5
P0336	Producto 336	Este es el producto número 336.	471.37	79	9	5
P0337	Producto 337	Este es el producto número 337.	174.21	200	4	5
P0338	Producto 338	Este es el producto número 338.	379.49	72	14	5
P0339	Producto 339	Este es el producto número 339.	131.75	4	12	5
P0340	Producto 340	Este es el producto número 340.	447.44	177	7	5
P0341	Producto 341	Este es el producto número 341.	457.03	184	9	5
P0342	Producto 342	Este es el producto número 342.	230.74	115	15	5
P0343	Producto 343	Este es el producto número 343.	113.65	188	10	5
P0344	Producto 344	Este es el producto número 344.	181.71	175	10	5
P0345	Producto 345	Este es el producto número 345.	180.22	159	4	5
P0346	Producto 346	Este es el producto número 346.	400.05	9	7	5
P0347	Producto 347	Este es el producto número 347.	227.2	12	17	5
P0348	Producto 348	Este es el producto número 348.	295.37	185	14	5
P0349	Producto 349	Este es el producto número 349.	489.88	127	3	5
P0350	Producto 350	Este es el producto número 350.	104.51	168	9	5
P0351	Producto 351	Este es el producto número 351.	488.48	50	11	5
P0352	Producto 352	Este es el producto número 352.	433	152	18	5
P0353	Producto 353	Este es el producto número 353.	144.94	3	6	5
P0354	Producto 354	Este es el producto número 354.	49.25	62	14	5
P0355	Producto 355	Este es el producto número 355.	20.34	31	5	5
P0356	Producto 356	Este es el producto número 356.	260.23	86	19	5
P0357	Producto 357	Este es el producto número 357.	481.26	183	13	5
P0358	Producto 358	Este es el producto número 358.	466.82	71	5	5
P0359	Producto 359	Este es el producto número 359.	156.9	139	2	5
P0360	Producto 360	Este es el producto número 360.	150.75	193	2	5
P0361	Producto 361	Este es el producto número 361.	54.82	175	4	5
P0362	Producto 362	Este es el producto número 362.	337.32	112	16	5
P0363	Producto 363	Este es el producto número 363.	50.18	200	19	5
P0364	Producto 364	Este es el producto número 364.	290.14	93	5	5
P0365	Producto 365	Este es el producto número 365.	350.58	52	13	5
P0366	Producto 366	Este es el producto número 366.	13.54	130	11	5
P0367	Producto 367	Este es el producto número 367.	365.81	113	1	5
P0368	Producto 368	Este es el producto número 368.	181.05	145	4	5
P0369	Producto 369	Este es el producto número 369.	368.13	47	3	5
P0370	Producto 370	Este es el producto número 370.	46.9	199	19	5
P0371	Producto 371	Este es el producto número 371.	277.41	58	4	5
P0372	Producto 372	Este es el producto número 372.	400.01	59	12	5
P0373	Producto 373	Este es el producto número 373.	487.58	111	5	5
P0374	Producto 374	Este es el producto número 374.	249.94	82	18	5
P0375	Producto 375	Este es el producto número 375.	467.77	185	6	5
P0376	Producto 376	Este es el producto número 376.	422.16	48	12	5
P0377	Producto 377	Este es el producto número 377.	189.44	148	12	5
P0378	Producto 378	Este es el producto número 378.	101.55	60	5	5
P0379	Producto 379	Este es el producto número 379.	117.86	168	10	5
P0380	Producto 380	Este es el producto número 380.	460.69	56	4	5
P0381	Producto 381	Este es el producto número 381.	261.95	46	4	5
P0382	Producto 382	Este es el producto número 382.	191.08	68	2	5
P0383	Producto 383	Este es el producto número 383.	166.54	141	9	5
P0384	Producto 384	Este es el producto número 384.	214.05	127	20	5
P0385	Producto 385	Este es el producto número 385.	17.09	120	4	5
P0386	Producto 386	Este es el producto número 386.	389.51	200	16	5
P0387	Producto 387	Este es el producto número 387.	328.98	46	6	5
P0388	Producto 388	Este es el producto número 388.	418.29	140	6	5
P0389	Producto 389	Este es el producto número 389.	325.03	192	13	5
P0390	Producto 390	Este es el producto número 390.	176.73	172	16	5
P0391	Producto 391	Este es el producto número 391.	497.04	183	1	5
P0392	Producto 392	Este es el producto número 392.	14.59	5	14	5
P0393	Producto 393	Este es el producto número 393.	130.09	20	3	5
P0394	Producto 394	Este es el producto número 394.	453.93	155	20	5
P0395	Producto 395	Este es el producto número 395.	443.24	98	6	5
P0396	Producto 396	Este es el producto número 396.	192.35	10	10	5
P0397	Producto 397	Este es el producto número 397.	252.56	172	2	5
P0398	Producto 398	Este es el producto número 398.	71.89	183	19	5
P0399	Producto 399	Este es el producto número 399.	308.24	115	4	5
P0400	Producto 400	Este es el producto número 400.	224.79	93	2	5
P0401	Producto 401	Este es el producto número 401.	481.62	107	12	5
P0402	Producto 402	Este es el producto número 402.	154.93	86	19	5
P0403	Producto 403	Este es el producto número 403.	55.11	93	8	5
P0404	Producto 404	Este es el producto número 404.	63.38	177	9	5
P0405	Producto 405	Este es el producto número 405.	205.37	129	11	5
P0406	Producto 406	Este es el producto número 406.	309.61	79	20	5
P0407	Producto 407	Este es el producto número 407.	266.8	177	20	5
P0408	Producto 408	Este es el producto número 408.	455.7	81	7	5
P0409	Producto 409	Este es el producto número 409.	38.86	82	19	5
P0410	Producto 410	Este es el producto número 410.	215.05	70	5	5
P0411	Producto 411	Este es el producto número 411.	196.72	109	4	5
P0412	Producto 412	Este es el producto número 412.	348.17	199	11	5
P0413	Producto 413	Este es el producto número 413.	403.75	62	19	5
P0414	Producto 414	Este es el producto número 414.	193.53	27	5	5
P0415	Producto 415	Este es el producto número 415.	435.33	111	14	5
P0416	Producto 416	Este es el producto número 416.	104.97	86	4	5
P0417	Producto 417	Este es el producto número 417.	163.67	107	18	5
P0418	Producto 418	Este es el producto número 418.	107.67	154	6	5
P0419	Producto 419	Este es el producto número 419.	175.21	82	3	5
P0420	Producto 420	Este es el producto número 420.	38.95	164	11	5
P0421	Producto 421	Este es el producto número 421.	52.2	72	20	5
P0422	Producto 422	Este es el producto número 422.	76.86	165	10	5
P0423	Producto 423	Este es el producto número 423.	61.22	50	9	5
P0424	Producto 424	Este es el producto número 424.	390.41	122	20	5
P0425	Producto 425	Este es el producto número 425.	348.15	74	9	5
P0426	Producto 426	Este es el producto número 426.	458.2	187	6	5
P0427	Producto 427	Este es el producto número 427.	304.06	139	11	5
P0428	Producto 428	Este es el producto número 428.	118.86	179	20	5
P0429	Producto 429	Este es el producto número 429.	441.72	86	3	5
P0430	Producto 430	Este es el producto número 430.	317.19	151	17	5
P0431	Producto 431	Este es el producto número 431.	95.58	74	13	5
P0432	Producto 432	Este es el producto número 432.	299.69	131	8	5
P0433	Producto 433	Este es el producto número 433.	60.61	45	13	5
P0434	Producto 434	Este es el producto número 434.	458.19	186	18	5
P0435	Producto 435	Este es el producto número 435.	374.5	83	16	5
P0436	Producto 436	Este es el producto número 436.	67.82	200	12	5
P0437	Producto 437	Este es el producto número 437.	282.8	71	7	5
P0438	Producto 438	Este es el producto número 438.	79.93	38	19	5
P0439	Producto 439	Este es el producto número 439.	332.83	117	6	5
P0440	Producto 440	Este es el producto número 440.	117.45	48	18	5
P0441	Producto 441	Este es el producto número 441.	324.02	196	4	5
P0442	Producto 442	Este es el producto número 442.	47.59	117	8	5
P0443	Producto 443	Este es el producto número 443.	122.47	70	19	5
P0444	Producto 444	Este es el producto número 444.	204.62	7	1	5
P0445	Producto 445	Este es el producto número 445.	211.93	68	15	5
P0446	Producto 446	Este es el producto número 446.	261.22	26	14	5
P0447	Producto 447	Este es el producto número 447.	202.28	13	13	5
P0448	Producto 448	Este es el producto número 448.	253.6	199	9	5
P0449	Producto 449	Este es el producto número 449.	151.3	13	2	5
P0450	Producto 450	Este es el producto número 450.	321.27	152	7	5
P0451	Producto 451	Este es el producto número 451.	187.45	34	13	5
P0452	Producto 452	Este es el producto número 452.	23.54	153	10	5
P0453	Producto 453	Este es el producto número 453.	419.95	82	14	5
P0454	Producto 454	Este es el producto número 454.	129.46	197	17	5
P0455	Producto 455	Este es el producto número 455.	479.37	81	16	5
P0456	Producto 456	Este es el producto número 456.	148.05	101	13	5
P0457	Producto 457	Este es el producto número 457.	63.84	164	9	5
P0458	Producto 458	Este es el producto número 458.	235.68	3	18	5
P0459	Producto 459	Este es el producto número 459.	344.73	84	7	5
P0460	Producto 460	Este es el producto número 460.	213.9	179	2	5
P0461	Producto 461	Este es el producto número 461.	65.47	55	13	5
P0462	Producto 462	Este es el producto número 462.	180.39	46	5	5
P0463	Producto 463	Este es el producto número 463.	258.82	157	7	5
P0464	Producto 464	Este es el producto número 464.	320.06	23	3	5
P0465	Producto 465	Este es el producto número 465.	168.46	179	4	5
P0466	Producto 466	Este es el producto número 466.	406.92	99	16	5
P0467	Producto 467	Este es el producto número 467.	493.02	151	15	5
P0468	Producto 468	Este es el producto número 468.	174.58	199	3	5
P0469	Producto 469	Este es el producto número 469.	71.85	122	1	5
P0470	Producto 470	Este es el producto número 470.	288.71	100	13	5
P0471	Producto 471	Este es el producto número 471.	127.9	157	12	5
P0472	Producto 472	Este es el producto número 472.	160.78	142	10	5
P0473	Producto 473	Este es el producto número 473.	468.44	51	10	5
P0474	Producto 474	Este es el producto número 474.	225.54	68	15	5
P0475	Producto 475	Este es el producto número 475.	382.92	30	17	5
P0476	Producto 476	Este es el producto número 476.	150.61	77	11	5
P0477	Producto 477	Este es el producto número 477.	356.56	44	11	5
P0478	Producto 478	Este es el producto número 478.	31.13	133	1	5
P0479	Producto 479	Este es el producto número 479.	474.96	2	8	5
P0480	Producto 480	Este es el producto número 480.	248.79	86	16	5
P0481	Producto 481	Este es el producto número 481.	183.17	14	19	5
P0482	Producto 482	Este es el producto número 482.	314.96	197	20	5
P0483	Producto 483	Este es el producto número 483.	139.36	44	6	5
P0484	Producto 484	Este es el producto número 484.	320.28	2	15	5
P0485	Producto 485	Este es el producto número 485.	324.92	85	14	5
P0486	Producto 486	Este es el producto número 486.	446.97	161	7	5
P0487	Producto 487	Este es el producto número 487.	203.09	36	20	5
P0488	Producto 488	Este es el producto número 488.	382.3	118	5	5
P0489	Producto 489	Este es el producto número 489.	284.63	87	10	5
P0490	Producto 490	Este es el producto número 490.	470.8	62	17	5
P0491	Producto 491	Este es el producto número 491.	401.83	83	14	5
P0492	Producto 492	Este es el producto número 492.	320.02	169	2	5
P0493	Producto 493	Este es el producto número 493.	230.63	46	12	5
P0494	Producto 494	Este es el producto número 494.	22.12	59	15	5
P0495	Producto 495	Este es el producto número 495.	267.34	87	8	5
P0496	Producto 496	Este es el producto número 496.	201.96	92	5	5
P0497	Producto 497	Este es el producto número 497.	193.4	194	18	5
P0498	Producto 498	Este es el producto número 498.	432.32	191	9	5
P0499	Producto 499	Este es el producto número 499.	41.03	18	4	5
P0500	Producto 500	Este es el producto número 500.	376.6	54	1	5
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuarios (id, nombre, correo, "contraseña", rol, fecha_registro) FROM stdin;
1	Administrador	admin@inventario.com	admin	admin	2025-05-07 14:27:36.187874
\.


--
-- Name: categorias_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categorias_id_seq', 1, true);


--
-- Name: movimientos_stock_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.movimientos_stock_id_seq', 16, true);


--
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 1, false);


--
-- Name: categorias categorias_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT categorias_nombre_key UNIQUE (nombre);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: movimientos_stock movimientos_stock_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimientos_stock
    ADD CONSTRAINT movimientos_stock_pkey PRIMARY KEY (id);


--
-- Name: productos productos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productos
    ADD CONSTRAINT productos_pkey PRIMARY KEY (codigo);


--
-- Name: usuarios usuarios_correo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_correo_key UNIQUE (correo);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: movimientos_stock movimientos_stock_codigo_producto_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimientos_stock
    ADD CONSTRAINT movimientos_stock_codigo_producto_fkey FOREIGN KEY (codigo_producto) REFERENCES public.productos(codigo);


--
-- Name: movimientos_stock movimientos_stock_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.movimientos_stock
    ADD CONSTRAINT movimientos_stock_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: productos productos_categoria_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productos
    ADD CONSTRAINT productos_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES public.categorias(id);


--
-- PostgreSQL database dump complete
--

