--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4 (Debian 15.4-2.pgdg120+1)
-- Dumped by pg_dump version 15.4 (Debian 15.4-2.pgdg120+1)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: process; Type: TABLE; Schema: public; Owner: mes_user
--

CREATE TABLE public.process (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    name character varying(100) NOT NULL,
    sequence integer NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL
);


ALTER TABLE public.process OWNER TO mes_user;

--
-- Name: process_id_seq; Type: SEQUENCE; Schema: public; Owner: mes_user
--

CREATE SEQUENCE public.process_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.process_id_seq OWNER TO mes_user;

--
-- Name: process_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mes_user
--

ALTER SEQUENCE public.process_id_seq OWNED BY public.process.id;


--
-- Name: product; Type: TABLE; Schema: public; Owner: mes_user
--

CREATE TABLE public.product (
    id bigint NOT NULL,
    code character varying(50) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    name character varying(100) NOT NULL,
    spec character varying(200),
    unit character varying(20) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL
);


ALTER TABLE public.product OWNER TO mes_user;

--
-- Name: product_id_seq; Type: SEQUENCE; Schema: public; Owner: mes_user
--

CREATE SEQUENCE public.product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_id_seq OWNER TO mes_user;

--
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mes_user
--

ALTER SEQUENCE public.product_id_seq OWNED BY public.product.id;


--
-- Name: station; Type: TABLE; Schema: public; Owner: mes_user
--

CREATE TABLE public.station (
    id bigint NOT NULL,
    code character varying(50) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    name character varying(100) NOT NULL,
    status character varying(20) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    process_id bigint NOT NULL,
    CONSTRAINT station_status_check CHECK (((status)::text = ANY ((ARRAY['RUNNING'::character varying, 'STOPPED'::character varying, 'FAULT'::character varying, 'MAINTENANCE'::character varying])::text[])))
);


ALTER TABLE public.station OWNER TO mes_user;

--
-- Name: station_id_seq; Type: SEQUENCE; Schema: public; Owner: mes_user
--

CREATE SEQUENCE public.station_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.station_id_seq OWNER TO mes_user;

--
-- Name: station_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mes_user
--

ALTER SEQUENCE public.station_id_seq OWNED BY public.station.id;


--
-- Name: work_order; Type: TABLE; Schema: public; Owner: mes_user
--

CREATE TABLE public.work_order (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    end_date timestamp(6) without time zone,
    order_date date NOT NULL,
    order_no character varying(50) NOT NULL,
    quantity integer NOT NULL,
    start_date timestamp(6) without time zone,
    status character varying(20) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    product_id bigint NOT NULL,
    CONSTRAINT work_order_status_check CHECK (((status)::text = ANY ((ARRAY['PLANNED'::character varying, 'IN_PROGRESS'::character varying, 'COMPLETED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.work_order OWNER TO mes_user;

--
-- Name: work_order_id_seq; Type: SEQUENCE; Schema: public; Owner: mes_user
--

CREATE SEQUENCE public.work_order_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.work_order_id_seq OWNER TO mes_user;

--
-- Name: work_order_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mes_user
--

ALTER SEQUENCE public.work_order_id_seq OWNED BY public.work_order.id;


--
-- Name: process id; Type: DEFAULT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.process ALTER COLUMN id SET DEFAULT nextval('public.process_id_seq'::regclass);


--
-- Name: product id; Type: DEFAULT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.product ALTER COLUMN id SET DEFAULT nextval('public.product_id_seq'::regclass);


--
-- Name: station id; Type: DEFAULT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.station ALTER COLUMN id SET DEFAULT nextval('public.station_id_seq'::regclass);


--
-- Name: work_order id; Type: DEFAULT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.work_order ALTER COLUMN id SET DEFAULT nextval('public.work_order_id_seq'::regclass);


--
-- Data for Name: process; Type: TABLE DATA; Schema: public; Owner: mes_user
--

COPY public.process (id, created_at, description, name, sequence, updated_at) FROM stdin;
1	2025-10-26 10:09:03.101777	센서 제조에 필요한 부품을 준비하는 공정	부품준비	1	2025-10-26 10:09:03.101827
2	2025-10-26 10:09:07.163757	부품을 조립하여 센서 모듈을 제작하는 공정	조립	2	2025-10-26 10:09:07.163776
3	2025-10-26 10:09:09.786166	조립된 센서 모듈의 품질을 검사하는 공정	검사	3	2025-10-26 10:09:09.786187
4	2025-10-26 10:09:13.507367	검사를 통과한 제품을 포장하는 공정	포장	4	2025-10-26 10:09:13.507384
\.


--
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: mes_user
--

COPY public.product (id, code, created_at, name, spec, unit, updated_at) FROM stdin;
1	TS100	2025-10-25 18:09:36.530213	온도센서 모듈 TS-100	10mm x 10mm	EA	2025-10-25 18:09:36.53026
2	HS200	2025-10-25 18:09:44.536692	습도센서 모듈 HS-200	15mm x 15mm	EA	2025-10-25 18:09:44.536711
3	PS300	2025-10-25 18:10:07.110298	압력센서 모듈 PS-300	20mm x 20mm	BOX	2025-10-25 18:10:07.110317
\.


--
-- Data for Name: station; Type: TABLE DATA; Schema: public; Owner: mes_user
--

COPY public.station (id, code, created_at, name, status, updated_at, process_id) FROM stdin;
1	STATION-A	2025-10-26 10:09:27.411719	부품준비 스테이션 A	RUNNING	2025-10-26 10:09:27.411738	1
2	STATION-B	2025-10-26 10:09:30.393617	조립 스테이션 B	RUNNING	2025-10-26 10:09:30.393635	2
3	STATION-C	2025-10-26 10:09:32.822111	검사 스테이션 C	STOPPED	2025-10-26 10:09:32.82217	3
4	STATION-D	2025-10-26 10:09:39.946625	포장 스테이션 D	RUNNING	2025-10-26 10:09:39.946641	4
\.


--
-- Data for Name: work_order; Type: TABLE DATA; Schema: public; Owner: mes_user
--

COPY public.work_order (id, created_at, end_date, order_date, order_no, quantity, start_date, status, updated_at, product_id) FROM stdin;
1	2025-10-26 10:23:16.15321	2025-10-26 10:23:37.476554	2025-01-15	WO-2025-001	1000	2025-10-26 10:23:34.784444	COMPLETED	2025-10-26 10:23:37.477458	1
2	2025-10-26 10:23:18.861819	\N	2025-01-15	WO-2025-002	500	2025-10-26 10:23:40.152577	IN_PROGRESS	2025-10-26 10:23:40.153188	2
4	2025-10-26 16:17:26.481009	\N	2025-10-26	WO-2025-004	1000	\N	PLANNED	2025-10-26 16:17:26.481095	1
3	2025-10-26 10:23:21.335543	\N	2025-01-16	WO-2025-003	800	2025-10-26 16:26:29.455478	IN_PROGRESS	2025-10-26 16:26:29.467941	3
\.


--
-- Name: process_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mes_user
--

SELECT pg_catalog.setval('public.process_id_seq', 4, true);


--
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mes_user
--

SELECT pg_catalog.setval('public.product_id_seq', 3, true);


--
-- Name: station_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mes_user
--

SELECT pg_catalog.setval('public.station_id_seq', 4, true);


--
-- Name: work_order_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mes_user
--

SELECT pg_catalog.setval('public.work_order_id_seq', 4, true);


--
-- Name: process process_pkey; Type: CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.process
    ADD CONSTRAINT process_pkey PRIMARY KEY (id);


--
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- Name: station station_pkey; Type: CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.station
    ADD CONSTRAINT station_pkey PRIMARY KEY (id);


--
-- Name: station uk_1mq0bkovmdvo63nfgcmtxrgd4; Type: CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.station
    ADD CONSTRAINT uk_1mq0bkovmdvo63nfgcmtxrgd4 UNIQUE (code);


--
-- Name: work_order uk_98nbpfoem7dridge9ravdx8wv; Type: CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT uk_98nbpfoem7dridge9ravdx8wv UNIQUE (order_no);


--
-- Name: product uk_h3w5r1mx6d0e5c6um32dgyjej; Type: CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT uk_h3w5r1mx6d0e5c6um32dgyjej UNIQUE (code);


--
-- Name: work_order work_order_pkey; Type: CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT work_order_pkey PRIMARY KEY (id);


--
-- Name: station fkeo67ow1pynkud46q5s582mc4q; Type: FK CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.station
    ADD CONSTRAINT fkeo67ow1pynkud46q5s582mc4q FOREIGN KEY (process_id) REFERENCES public.process(id);


--
-- Name: work_order fkl0to285uaxdg2siljxu4614q5; Type: FK CONSTRAINT; Schema: public; Owner: mes_user
--

ALTER TABLE ONLY public.work_order
    ADD CONSTRAINT fkl0to285uaxdg2siljxu4614q5 FOREIGN KEY (product_id) REFERENCES public.product(id);


--
-- PostgreSQL database dump complete
--

