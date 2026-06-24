--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: add_two_number(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.add_two_number(numone integer, numtwo integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    ans INTEGER;
BEGIN
    ans := numOne + numTwo;
    RETURN ans;
END;
$$;


ALTER FUNCTION public.add_two_number(numone integer, numtwo integer) OWNER TO postgres;

--
-- Name: fn_get_available_vehicle_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_available_vehicle_list(p_vehicleid bigint DEFAULT NULL::bigint, p_searchstring character varying DEFAULT NULL::character varying, p_page integer DEFAULT 0, p_size integer DEFAULT 10) RETURNS TABLE(sr_no bigint, vehicle_id bigint, users_name character varying, user_role character varying, vehicle_number character varying, vehicle_type character varying, vehicle_model character varying, fuel_type character varying, load_capacity double precision, last_service_date date, next_service_due date)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY v.id)::bigint AS sr_no,
        v.id::bigint AS vehicle_id,
       COALESCE(CONCAT(u.first_name, ' ', u.last_name), 'Unassigned')::character varying AS users_name,

        r.role::character varying AS user_role,
        v.vehicle_number::character varying,
        v.vehicle_type::character varying,
        v.vehicle_model::character varying,
        v.fuel_type::character varying,
        v.load_capacity::double precision,
        v.last_service_date::date,
        v.next_service_due_date::date AS next_service_due
    FROM vehicle v
   LEFT JOIN mt_users u ON v.users_id = u.id AND u.role_id IN (1,2,3)
	LEFT JOIN mt_roles r ON u.role_id = r.id
    WHERE v.is_removed = false
	AND u.role_id IN (1,2,3)
	AND v.is_added = true
      AND (
          -- Either no ID filter is provided, or the ID matches
          (p_vehicleid IS NULL OR v.id = p_vehicleid)
      )
      AND (
          -- Either no search string is provided, or one of the fields matches
          (p_searchstring IS NULL OR
          v.vehicle_number ILIKE '%' || p_searchstring || '%' OR
          v.vehicle_type ILIKE '%' || p_searchstring || '%' OR
          v.vehicle_model ILIKE '%' || p_searchstring || '%' OR
          v.fuel_type::text ILIKE '%' || p_searchstring || '%' OR
          CONCAT(u.first_name, ' ', u.last_name) ILIKE '%' || p_searchstring || '%' OR
          r.role ILIKE '%' || p_searchstring || '%')
      )
    ORDER BY v.id
    OFFSET (p_page * p_size)
    LIMIT p_size;
END;
$$;


ALTER FUNCTION public.fn_get_available_vehicle_list(p_vehicleid bigint, p_searchstring character varying, p_page integer, p_size integer) OWNER TO postgres;

--
-- Name: fn_get_customer_by_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_customer_by_id(customerid bigint) RETURNS TABLE(id bigint, customername character varying, mobilenumber character varying, address character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        c.id,
        c.customer_name,
        c.mobile_number,
        c.address,
        c.is_active
    FROM mt_customers c
    WHERE c.id = customerId;
END;
$$;


ALTER FUNCTION public.fn_get_customer_by_id(customerid bigint) OWNER TO postgres;

--
-- Name: fn_get_customer_list_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_customer_list_count(customerid bigint, searchstring character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    totalListCount INTEGER;
BEGIN
    SELECT COUNT(*) INTO totalListCount
    FROM mt_customers c
    WHERE c.is_delete = FALSE
      AND (customerId IS NULL OR c.id = customerId)
      AND (searchstring IS NULL OR c.customer_name ILIKE CONCAT('%', searchstring, '%'));

    RETURN totalListCount;
END;
$$;


ALTER FUNCTION public.fn_get_customer_list_count(customerid bigint, searchstring character varying) OWNER TO postgres;

--
-- Name: fn_get_customers_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_customers_list(customerid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, customer_name character varying, mobile_number character varying, address character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY c.id) AS srNo,
        c.id,
        c.customer_name,
		c.mobile_number,
        c.address,
        c.is_active
    FROM mt_customers c
    WHERE c.is_delete = false
      AND (customerId IS NULL OR c.id = customerId)
      AND (searchstring IS NULL OR c.customer_name ILIKE '%' || searchstring || '%')
    ORDER BY c.id
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_customers_list(customerid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_daily_assignments(bigint, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_daily_assignments(delivery_person_id bigint DEFAULT NULL::bigint, assigned_by_id bigint DEFAULT NULL::bigint) RETURNS TABLE(assignment_id bigint, assigned_by_name text, delivery_person_name text, product_name text, category_name text, quantity_assigned integer, unit_price double precision)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        da.id AS assignment_id,
        CONCAT(assigned_by.first_name, ' ', assigned_by.last_name) AS assigned_by_name,
        delivery_person.first_name AS delivery_person_name,
        p.product_name,
        pc.category_name,
        dad.quantity_assigned,
        dad.unit_price
    FROM daily_assignment da
    JOIN daily_assignment_details dad ON dad.daily_assignment = da.id
    JOIN mt_users assigned_by ON assigned_by.id = da.assigned_by_id
    JOIN mt_users delivery_person ON delivery_person.id = da.delivery_person_id
    JOIN mt_products p ON p.id = dad.products_id
    JOIN mt_product_category pc ON pc.id = dad.product_category_id
    WHERE 
        (delivery_person_id IS NULL OR da.delivery_person_id = delivery_person_id)
        AND (assigned_by_id IS NULL OR da.assigned_by_id = assigned_by_id);
END;
$$;


ALTER FUNCTION public.fn_get_daily_assignments(delivery_person_id bigint, assigned_by_id bigint) OWNER TO postgres;

--
-- Name: fn_get_daily_assignments_detailed(bigint, bigint, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_daily_assignments_detailed(p_delivery_person_id bigint DEFAULT NULL::bigint, p_assigned_by_id bigint DEFAULT NULL::bigint, p_page integer DEFAULT 0, p_size integer DEFAULT 10) RETURNS TABLE(assignment_id bigint, assigned_by_id_out bigint, assigned_by_first_name character varying, assigned_by_last_name character varying, assigned_by_mobile character varying, assigned_by_aadhar character varying, assigned_by_photo character varying, assigned_by_username character varying, assigned_by_is_active boolean, assigned_by_role character varying, delivery_person_id_out bigint, delivery_first_name character varying, delivery_last_name character varying, delivery_mobile character varying, delivery_aadhar character varying, delivery_photo character varying, delivery_username character varying, delivery_is_active boolean, delivery_role character varying, product_id bigint, product_name character varying, product_is_active boolean, category_name character varying, category_description character varying, category_is_active boolean, quantity_assigned integer, unit_price double precision)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        da.id AS assignment_id,
        ab.id AS assigned_by_id_out,
        ab.first_name, ab.last_name, ab.mobile_number, ab.aadhar_card_number, ab.photo_path, ab.username, ab.is_active,
        abr.role,
        dp.id AS delivery_person_id_out,
        dp.first_name, dp.last_name, dp.mobile_number, dp.aadhar_card_number, dp.photo_path, dp.username, dp.is_active,
        dpr.role,
        p.id AS product_id, p.product_name, p.is_active,
        pc.category_name, pc.description, pc.is_active,
        dad.quantity_assigned, dad.unit_price
  FROM daily_assignment da
LEFT JOIN daily_assignment_details dad ON dad.daily_assignment = da.id
LEFT JOIN mt_users ab ON ab.id = da.assigned_by_id
LEFT JOIN mt_roles abr ON ab.role_id = abr.id
LEFT JOIN mt_users dp ON dp.id = da.delivery_person_id
LEFT JOIN mt_roles dpr ON dp.role_id = dpr.id
LEFT JOIN mt_products p ON p.id = dad.product_id
LEFT JOIN mt_product_category pc ON pc.id = dad.product_category_id

    WHERE 
        (p_delivery_person_id IS NULL OR da.delivery_person_id = p_delivery_person_id)
        AND (p_assigned_by_id IS NULL OR da.assigned_by_id = p_assigned_by_id)
    LIMIT p_size OFFSET p_page * p_size;
END;
$$;


ALTER FUNCTION public.fn_get_daily_assignments_detailed(p_delivery_person_id bigint, p_assigned_by_id bigint, p_page integer, p_size integer) OWNER TO postgres;

--
-- Name: fn_get_daily_assignments_details(bigint, bigint, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_daily_assignments_details(p_delivery_person_id bigint DEFAULT NULL::bigint, p_assigned_by_id bigint DEFAULT NULL::bigint, p_page integer DEFAULT 0, p_size integer DEFAULT 10) RETURNS TABLE(assignment_id bigint, assigned_by_id bigint, assigned_by_first_name character varying, assigned_by_last_name character varying, assigned_by_mobile character varying, assigned_by_aadhar character varying, assigned_by_photo character varying, assigned_by_username character varying, assigned_by_is_active boolean, assigned_by_role character varying, delivery_person_id bigint, delivery_first_name character varying, delivery_last_name character varying, delivery_mobile character varying, delivery_aadhar character varying, delivery_photo character varying, delivery_username character varying, delivery_is_active boolean, delivery_role character varying, product_details jsonb)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        da.id,
        ab.id,
        ab.first_name, ab.last_name, ab.mobile_number, ab.aadhar_card_number, ab.photo_path, ab.username, ab.is_active,
        abr.role,
        dp.id,
        dp.first_name, dp.last_name, dp.mobile_number, dp.aadhar_card_number, dp.photo_path, dp.username, dp.is_active,
        dpr.role,
        COALESCE(
            jsonb_agg(
                jsonb_build_object(
                    'product_id', p.id,
                    'product_name', p.product_name,
                    'product_is_active', p.is_active,
                    'category_name', pc.category_name,
                    'category_description', pc.description,
                    'category_is_active', pc.is_active,
                    'quantity_assigned', dad.quantity_assigned,
                    'unit_price', dad.unit_price
                )
            ) FILTER (WHERE dad.id IS NOT NULL), '[]'
        ) AS product_details
    FROM daily_assignment da
    LEFT JOIN daily_assignment_details dad ON dad.daily_assignment = da.id
    LEFT JOIN mt_products p ON p.id = dad.product_id
    LEFT JOIN mt_product_category pc ON pc.id = dad.product_category_id
    LEFT JOIN mt_users ab ON ab.id = da.assigned_by_id
    LEFT JOIN mt_roles abr ON ab.role_id = abr.id
    LEFT JOIN mt_users dp ON dp.id = da.delivery_person_id
    LEFT JOIN mt_roles dpr ON dp.role_id = dpr.id
    WHERE 
        (p_delivery_person_id IS NULL OR da.delivery_person_id = p_delivery_person_id)
        AND (p_assigned_by_id IS NULL OR da.assigned_by_id = p_assigned_by_id)
    GROUP BY 
        da.id, ab.id, ab.first_name, ab.last_name, ab.mobile_number, ab.aadhar_card_number, ab.photo_path, ab.username, ab.is_active,
        abr.role, dp.id, dp.first_name, dp.last_name, dp.mobile_number, dp.aadhar_card_number, dp.photo_path, dp.username, dp.is_active,
        dpr.role
    ORDER BY da.id
    LIMIT p_size OFFSET p_page * p_size;
END;
$$;


ALTER FUNCTION public.fn_get_daily_assignments_details(p_delivery_person_id bigint, p_assigned_by_id bigint, p_page integer, p_size integer) OWNER TO postgres;

--
-- Name: fn_get_daily_assignments_details_by_date(date, date); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_daily_assignments_details_by_date(p_from_date date DEFAULT NULL::date, p_to_date date DEFAULT NULL::date) RETURNS TABLE(assignment_id bigint, assigned_by_id_out bigint, assigned_by_first_name character varying, assigned_by_last_name character varying, assigned_by_mobile character varying, assigned_by_role character varying, delivery_person_id_out bigint, status_id bigint, delivery_first_name character varying, delivery_last_name character varying, delivery_mobile character varying, delivery_role character varying, product_id bigint, product_name character varying, category_name character varying, category_description character varying, quantity_assigned integer, unit_price double precision, assignment_created_date timestamp without time zone, customer_id bigint, customer_name character varying, customer_mobile character varying, customer_address character varying, agency_point_id bigint, point_holder_name character varying, agency_point_mobile character varying, agency_point_address character varying, agency_point_name character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
	RETURN QUERY
	SELECT 
		da.id AS assignment_id,
		ab.id AS assigned_by_id_out,
		ab.first_name AS assigned_by_first_name,
		ab.last_name AS assigned_by_last_name,
		ab.mobile_number AS assigned_by_mobile,
		abr.role AS assigned_by_role,
		dp.id AS delivery_person_id_out,
		da.status_id AS status_id,
		dp.first_name AS delivery_first_name,
		dp.last_name AS delivery_last_name,
		dp.mobile_number AS delivery_mobile,
		dpr.role AS delivery_role,
		p.id AS product_id,
		p.product_name,
		pc.category_name,
		pc.description AS category_description,
		dad.quantity_assigned,
		dad.unit_price,
		da.created_date AS assignment_created_date,

		-- Customer Info (only if is_customer = true)
		c.id AS customer_id,
		c.customer_name,
		c.mobile_number AS customer_mobile,
		c.address AS customer_address,

		-- Agency Point Info (only if is_point = true)
		ap.id AS agency_point_id,
		ap.point_holder_name,
		ap.mobile_number AS agency_point_mobile,
		ap.address AS agency_point_address,
		ap.point_name AS agency_point_name

	FROM daily_assignment da
	LEFT JOIN daily_assignment_details dad ON dad.daily_assignment = da.id
	LEFT JOIN mt_users ab ON ab.id = da.assigned_by_id
	LEFT JOIN mt_roles abr ON ab.role_id = abr.id
	LEFT JOIN mt_users dp ON dp.id = da.delivery_person_id
	LEFT JOIN mt_roles dpr ON dp.role_id = dpr.id
	LEFT JOIN mt_products p ON p.id = dad.product_id
	LEFT JOIN mt_product_category pc ON pc.id = dad.product_category_id
	LEFT JOIN mt_customers c ON c.id = da.customer_id AND da.is_customer = true
	LEFT JOIN mt_agency_points ap ON ap.id = da.agency_point_id AND da.is_point = true
	WHERE 
		(p_from_date IS NULL OR da.last_modified_date::DATE >= p_from_date)
		AND da.is_delete = false
		AND (p_to_date IS NULL OR da.last_modified_date::DATE <= p_to_date);
END;
$$;


ALTER FUNCTION public.fn_get_daily_assignments_details_by_date(p_from_date date, p_to_date date) OWNER TO postgres;

--
-- Name: fn_get_daily_assignments_list(bigint, text, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_daily_assignments_list(p_id bigint DEFAULT NULL::bigint, p_search_string text DEFAULT NULL::text, p_page integer DEFAULT 0, p_size integer DEFAULT 10) RETURNS TABLE(assignment_id bigint, assigned_by_id_out bigint, assigned_by_first_name character varying, assigned_by_last_name character varying, assigned_by_mobile character varying, assigned_by_role character varying, delivery_person_id_out bigint, delivery_first_name character varying, delivery_last_name character varying, delivery_mobile character varying, delivery_role character varying, product_id bigint, product_name character varying, product_is_active boolean, category_name character varying, category_description character varying, category_is_active boolean, quantity_assigned integer, unit_price double precision, assignment_created_date timestamp without time zone, customer_id bigint, customer_name character varying, customer_mobile character varying, customer_address character varying, agency_point_id bigint, point_holder_name character varying, agency_point_mobile character varying, agency_point_address character varying, agency_point_name character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        da.id AS assignment_id,
        ab.id AS assigned_by_id_out,
        ab.first_name AS assigned_by_first_name,
        ab.last_name AS assigned_by_last_name,
        ab.mobile_number AS assigned_by_mobile,
        abr.role AS assigned_by_role,
        dp.id AS delivery_person_id_out,
        dp.first_name AS delivery_first_name,
        dp.last_name AS delivery_last_name,
        dp.mobile_number AS delivery_mobile,
        dpr.role AS delivery_role,
        p.id AS product_id,
        p.product_name,
        p.is_active AS product_is_active,
        pc.category_name,
        pc.description AS category_description,
        pc.is_active AS category_is_active,
        dad.quantity_assigned,
        dad.unit_price,
        da.created_date AS assignment_created_date,
        c.id AS customer_id,
        c.customer_name,
        c.mobile_number AS customer_mobile,
        c.address AS customer_address,
        ap.id AS agency_point_id,
        ap.point_holder_name,
        ap.mobile_number AS agency_point_mobile,
        ap.address AS agency_point_address,
        ap.point_name AS agency_point_name
    FROM daily_assignment da
    LEFT JOIN daily_assignment_details dad ON dad.daily_assignment = da.id
    LEFT JOIN mt_users ab ON ab.id = da.assigned_by_id
    LEFT JOIN mt_roles abr ON ab.role_id = abr.id
    LEFT JOIN mt_users dp ON dp.id = da.delivery_person_id
    LEFT JOIN mt_roles dpr ON dp.role_id = dpr.id
    LEFT JOIN mt_products p ON p.id = dad.product_id
    LEFT JOIN mt_product_category pc ON pc.id = dad.product_category_id
    LEFT JOIN mt_customers c ON c.id = da.customer_id AND da.is_customer = TRUE
    LEFT JOIN mt_agency_points ap ON ap.id = da.agency_point_id AND da.is_point = TRUE
    WHERE
        (p_id IS NULL OR da.id = p_id)
        AND (p_search_string IS NULL OR (
            c.customer_name ILIKE '%' || p_search_string || '%'
            OR ap.point_name ILIKE '%' || p_search_string || '%'
            OR p.product_name ILIKE '%' || p_search_string || '%'
        ))
    ORDER BY da.created_date DESC
    LIMIT p_size OFFSET p_page * p_size;
END;
$$;


ALTER FUNCTION public.fn_get_daily_assignments_list(p_id bigint, p_search_string text, p_page integer, p_size integer) OWNER TO postgres;

--
-- Name: fn_get_daily_assignments_list_count(bigint, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_daily_assignments_list_count(p_id bigint DEFAULT NULL::bigint, p_search_string text DEFAULT NULL::text) RETURNS bigint
    LANGUAGE plpgsql
    AS $$
DECLARE
    total_count bigint;
BEGIN
    SELECT COUNT(DISTINCT da.id) INTO total_count
    FROM daily_assignment da
    LEFT JOIN daily_assignment_details dad ON dad.daily_assignment = da.id
    LEFT JOIN mt_products p ON p.id = dad.product_id
    LEFT JOIN mt_customers c ON c.id = da.customer_id AND da.is_customer = TRUE
    LEFT JOIN mt_agency_points ap ON ap.id = da.agency_point_id AND da.is_point = TRUE
    WHERE
        (p_id IS NULL OR da.id = p_id)
        AND (p_search_string IS NULL OR (
            c.customer_name ILIKE '%' || p_search_string || '%'
            OR ap.point_name ILIKE '%' || p_search_string || '%'
            OR p.product_name ILIKE '%' || p_search_string || '%'
        ));

    RETURN total_count;
END;
$$;


ALTER FUNCTION public.fn_get_daily_assignments_list_count(p_id bigint, p_search_string text) OWNER TO postgres;

--
-- Name: fn_get_delivery_boys_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_delivery_boys_list(userid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, namee text, mobilenumber character varying, username character varying, isactive boolean, rolee character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY u.id) AS srno,
        u.id,
        CONCAT(u.first_name, ' ', u.last_name) AS namee,
        u.mobile_number AS mobilenumber,
        u.username,
        u.is_active AS isactive,
        r.role AS rolee
    FROM mt_users u
    LEFT JOIN mt_roles r ON u.role_id = r.id
    WHERE u.is_delete = false
      AND u.role_id IN (4, 8)
      AND (userid IS NULL OR u.id = userid)
      AND (searchstring IS NULL OR CONCAT(u.first_name, ' ', u.last_name) ILIKE '%' || searchstring || '%')
    ORDER BY u.id
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_delivery_boys_list(userid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_live_inventory(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_live_inventory(p_id bigint, p_search character varying, p_page integer, p_size integer) RETURNS TABLE(id bigint, product_id bigint, product_name character varying, category_id bigint, category_name character varying, total_quantity integer, filled integer, unfilled integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        lis.id,
        p.id AS product_id,
        p.product_name,
        c.id AS category_id,
        c.category_name,
        lis.total_quantity,
        lis.filled_tank,
        lis.un_filled_tank
    FROM 
        live_inventory_stocks lis
    JOIN 
        mt_products p ON lis.product_id = p.id
    JOIN 
        mt_product_category c ON lis.product_category_id = c.id
    WHERE 
        (p_id IS NULL OR lis.product_category_id = p_id)
        AND (
            p_search IS NULL OR 
            LOWER(p.product_name) LIKE LOWER('%' || p_search || '%') OR 
            LOWER(c.category_name) LIKE LOWER('%' || p_search || '%')
        )
        AND lis.is_delete = false
        AND lis.is_active = true
    ORDER BY lis.id DESC
    LIMIT p_size OFFSET (p_page * p_size);
END;
$$;


ALTER FUNCTION public.fn_get_live_inventory(p_id bigint, p_search character varying, p_page integer, p_size integer) OWNER TO postgres;

--
-- Name: fn_get_live_inventory_count(bigint, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_live_inventory_count(p_id bigint, p_search text) RETURNS bigint
    LANGUAGE plpgsql
    AS $$
DECLARE
    total BIGINT;
BEGIN
    SELECT COUNT(*) INTO total
    FROM 
        live_inventory_stocks lis
    JOIN 
        mt_products p ON lis.product_id = p.id
    JOIN 
        mt_product_category c ON lis.product_category_id = c.id
    WHERE 
        (p_id IS NULL OR lis.product_category_id = p_id)
        AND (
            p_search IS NULL OR 
            LOWER(p.product_name) LIKE LOWER('%' || p_search || '%') OR 
            LOWER(c.category_name) LIKE LOWER('%' || p_search || '%')
        );

    RETURN total;
END;
$$;


ALTER FUNCTION public.fn_get_live_inventory_count(p_id bigint, p_search text) OWNER TO postgres;

--
-- Name: fn_get_new_connection_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_new_connection_list(p_connection_id bigint, p_search_text character varying, p_page integer, p_size integer) RETURNS TABLE(sr_no bigint, connection_id bigint, customer_id bigint, customer_name character varying, mobile_number character varying, is_new_connection boolean, is_dbc boolean, is_inventory_buy boolean, is_cash boolean, cash_amount double precision, is_online boolean, online_amount double precision, created_by bigint, last_modified_by bigint, product_id bigint, product_name character varying, quantity integer, unit_price double precision)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY nc.created_date DESC) AS sr_no,
        nc.id AS connection_id,
        c.id AS customer_id,
        c.customer_name,
        c.mobile_number,
        nc.is_new_connection,
        nc.is_dbc,
        nc.is_inventory_buy,
        nc.is_cash,
        nc.cash_amount,
        nc.is_online,
        nc.online_amount,
        nc.created_by,
        nc.last_modified_by,
        p.id AS product_id,
        p.product_name,
        ncd.quantity,
        ncd.unit_price
    FROM new_connection nc
    JOIN mt_customers c ON nc.customer_id = c.id
    LEFT JOIN mt_new_connections_details ncd ON ncd.new_connection_id = nc.id
    LEFT JOIN mt_products p ON p.id = ncd.product_id
    WHERE (p_connection_id IS NULL OR nc.id = p_connection_id)
      AND (
          p_search_text IS NULL OR
          c.customer_name ILIKE CONCAT('%', p_search_text, '%') OR
          c.mobile_number ILIKE CONCAT('%', p_search_text, '%')
      )
    ORDER BY nc.created_date DESC
    OFFSET p_page * p_size
    LIMIT p_size;
END;
$$;


ALTER FUNCTION public.fn_get_new_connection_list(p_connection_id bigint, p_search_text character varying, p_page integer, p_size integer) OWNER TO postgres;

--
-- Name: fn_get_new_connection_list_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_new_connection_list_count(p_connection_id bigint, p_search_text character varying) RETURNS bigint
    LANGUAGE plpgsql
    AS $$
DECLARE
    total BIGINT;
BEGIN
    SELECT COUNT(*)
    INTO total
    FROM new_connection nc
    JOIN mt_customers c ON nc.customer_id = c.id
    WHERE (p_connection_id IS NULL OR nc.id = p_connection_id)
      AND (
          p_search_text IS NULL OR
          c.customer_name ILIKE CONCAT('%', p_search_text, '%') OR
          c.mobile_number ILIKE CONCAT('%', p_search_text, '%')
      );
    RETURN total;
END;
$$;


ALTER FUNCTION public.fn_get_new_connection_list_count(p_connection_id bigint, p_search_text character varying) OWNER TO postgres;

--
-- Name: fn_get_point_by_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_point_by_id(pointid bigint) RETURNS TABLE(id bigint, point_holder_name character varying, mobile_number character varying, address character varying, point_name character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
    SELECT 
        p.id as id,
        p.point_holder_name as point_holder_name,  -- corrected alias name
        p.mobile_number as mobile_number,
        p.address as address,
        p.point_name as point_name,
        p.is_active as is_active
    FROM
        mt_agency_points p
    WHERE
        p.id = pointid;  -- You need a condition to filter by pointid.
END;
$$;


ALTER FUNCTION public.fn_get_point_by_id(pointid bigint) OWNER TO postgres;

--
-- Name: fn_get_point_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_point_list(pointid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, pointholdername character varying, mobilenumber character varying, address character varying, pointname character varying, isactive boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY p.id) AS srno,
        p.id,
        p.point_holder_name,
        p.mobile_number,
        p.address,
        p.point_name,
		p.is_active
    FROM mt_agency_points p
    WHERE p.is_delete = false
      AND (pointId IS NULL OR p.id = pointId)
      AND (searchString IS NULL OR p.point_name ILIKE '%' || searchString || '%')
    ORDER BY p.id
    OFFSET (page * size)
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_point_list(pointid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_point_list_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_point_list_count(pointid bigint, searchstring character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    totalListCount integer;
BEGIN
    SELECT COUNT(*) INTO totalListCount
    FROM mt_agency_points point
    WHERE point.is_delete = false
      AND (pointId IS NULL OR point.id = pointId)
      AND (searchstring IS NULL OR point.point_name ILIKE '%' || searchstring || '%');

    RETURN totalListCount;
END;
$$;


ALTER FUNCTION public.fn_get_point_list_count(pointid bigint, searchstring character varying) OWNER TO postgres;

--
-- Name: fn_get_product_by_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_product_by_id(productidd bigint) RETURNS TABLE(productid bigint, productname character varying, product_price double precision, productisactive boolean, productcategory text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id,
        p.product_name,
		p.product_price,
        p.is_active,
        json_build_object(
		'id', c.id,
        'category', c.category_name
		)::text as productCategory
    FROM mt_products p
    LEFT JOIN mt_product_category c ON p.product_category_id = c.id
    WHERE p.id = productIdd and p.is_delete = false;
END;
$$;


ALTER FUNCTION public.fn_get_product_by_id(productidd bigint) OWNER TO postgres;

--
-- Name: fn_get_product_category_by_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_product_category_by_id(productcategoryid bigint) RETURNS TABLE(id bigint, category_name character varying, description character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        pc.id,
        pc.category_name,
        pc.description,
        pc.is_active
    FROM mt_product_category pc
    WHERE pc.id = productCategoryId
    AND pc.is_delete = false;
END;
$$;


ALTER FUNCTION public.fn_get_product_category_by_id(productcategoryid bigint) OWNER TO postgres;

--
-- Name: fn_get_product_category_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_product_category_list(productcategoryid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, category_name character varying, description character varying, isactive boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY pc.id) AS srNo,
        pc.id,
        pc.category_name,
        pc.description,
        pc.is_active
    FROM mt_product_category pc
    WHERE pc.is_delete = false
      AND (productCategoryId IS NULL OR pc.id = productCategoryId)
      AND (searchstring IS NULL OR pc.category_name ILIKE '%' || searchstring || '%')
    ORDER BY pc.id
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_product_category_list(productcategoryid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_product_category_list_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_product_category_list_count(productcategoryid bigint, searchstring character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    totalListCount integer;
BEGIN
    SELECT COUNT(*) INTO totalListCount
    FROM mt_product_category pc
    WHERE pc.is_delete = false
      AND (productCategoryId IS NULL OR pc.id = productCategoryId)
      AND (searchstring IS NULL OR pc.category_name ILIKE '%' || searchstring || '%');

    RETURN totalListCount;
END;
$$;


ALTER FUNCTION public.fn_get_product_category_list_count(productcategoryid bigint, searchstring character varying) OWNER TO postgres;

--
-- Name: fn_get_product_count_list(bigint, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_product_count_list(p_id bigint, p_search_string text) RETURNS bigint
    LANGUAGE plpgsql
    AS $$
DECLARE
    product_count BIGINT;
BEGIN
    SELECT COUNT(*)
    INTO product_count
    FROM mt_products mp
    JOIN mt_product_category pc ON mp.product_category_id = pc.id
    WHERE 
        mp.is_delete = FALSE
        AND pc.is_delete = FALSE
        AND (p_id IS NULL OR mp.id = p_id)
        AND (p_search_string IS NULL OR LOWER(mp.product_name) LIKE '%' || LOWER(p_search_string) || '%');

    RETURN product_count;
END;
$$;


ALTER FUNCTION public.fn_get_product_count_list(p_id bigint, p_search_string text) OWNER TO postgres;

--
-- Name: fn_get_product_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_product_list(productid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, product_name character varying, product_price double precision, category_id bigint, category_name character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        ROW_NUMBER() OVER (ORDER BY mp.id) AS srNo,
        mp.id,
        mp.product_name,
		mp.product_price,
        pc.id AS category_id,
        pc.category_name,
        mp.is_active
    FROM mt_products mp
    JOIN mt_product_category pc ON mp.product_category_id = pc.id
    WHERE 
        mp.is_delete = FALSE
        AND pc.is_delete = FALSE
        AND (productid IS NULL OR mp.id = productid)
        AND (searchstring IS NULL OR LOWER(mp.product_name) LIKE '%' || LOWER(searchstring) || '%')
    ORDER BY mp.product_name
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_product_list(productid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_role_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_role_list(roleid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, role character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY r.id) AS srNo,
        r.id,
        r.role,
       r.is_active
    FROM mt_roles r
    WHERE r.is_delete = false
      AND (roleId IS NULL OR r.id = roleId)
      AND (searchstring IS NULL OR r.role ILIKE '%' || searchstring || '%')
    ORDER BY r.id
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_role_list(roleid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_role_list_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_role_list_count(roleid bigint, searchstring character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    totalListCount INTEGER;
BEGIN
    SELECT COUNT(*) INTO totalListCount
    FROM mt_roles r
    WHERE r.is_delete = FALSE
      AND (roleId IS NULL OR r.id = roleId)
      AND (searchstring IS NULL OR r.role ILIKE CONCAT('%', searchstring, '%'));

    RETURN totalListCount;
END;
$$;


ALTER FUNCTION public.fn_get_role_list_count(roleid bigint, searchstring character varying) OWNER TO postgres;

--
-- Name: fn_get_service_type_by_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_service_type_by_id(servicetypeid bigint) RETURNS TABLE(id bigint, servicename character varying, servicerate double precision, description character varying, isactive boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        s.id,
        s.service_name,
        s.service_rate,
        s.description,
        s.is_active
    FROM mt_service_types s
    WHERE s.id = serviceTypeId;
END;
$$;


ALTER FUNCTION public.fn_get_service_type_by_id(servicetypeid bigint) OWNER TO postgres;

--
-- Name: fn_get_service_type_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_service_type_count(servicetypeid bigint, searchstring character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    totalListCount INTEGER;
BEGIN
    SELECT COUNT(*) INTO totalListCount
    FROM mt_service_types s
    WHERE s.is_delete = FALSE
      AND (serviceTypeId IS NULL OR s.id = serviceTypeId)
      AND (searchstring IS NULL OR s.service_name ILIKE CONCAT('%', searchstring, '%'));

    RETURN totalListCount;
END;
$$;


ALTER FUNCTION public.fn_get_service_type_count(servicetypeid bigint, searchstring character varying) OWNER TO postgres;

--
-- Name: fn_get_service_types_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_service_types_list(servicetypeid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, service_name character varying, service_rate double precision, description character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY s.id) AS srno,
        s.id,
        s.service_name,
        s.service_rate,
        s.description,
        s.is_active
    FROM mt_service_types s
    WHERE s.is_delete = FALSE
      AND (serviceTypeId IS NULL OR s.id = serviceTypeId)
      AND (searchstring IS NULL OR s.service_name ILIKE CONCAT('%', searchstring, '%'))
    ORDER BY s.id
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_service_types_list(servicetypeid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_status_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_status_list(statusid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, status character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        ROW_NUMBER() OVER (ORDER BY s.id) AS srno,
        s.id,
        s.status,
        s.is_active
    FROM mt_status s
    WHERE s.is_delete = FALSE
      AND (statusId IS NULL OR s.id = statusId)   -- ✅ Allow optional filter by ID
      AND (searchstring IS NULL OR LOWER(s.status) LIKE '%' || LOWER(searchstring) || '%')
    ORDER BY srno
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_status_list(statusid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_user(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_user(userid bigint) RETURNS TABLE(id bigint, firstname character varying, rolee text)
    LANGUAGE plpgsql
    AS $$
begin
	return query
	select
	u.id as id,
	u.first_name as firstName,
	json_build_object(
		'role', r.role
	)::text as rolee
	from 
	mt_users u 
	left join mt_roles r on u.role_id = r.id
	where u.is_delete = false and u.id = userId;
end;
$$;


ALTER FUNCTION public.fn_get_user(userid bigint) OWNER TO postgres;

--
-- Name: fn_get_user_by_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_user_by_id(userid bigint) RETURNS TABLE(id bigint, firstname character varying, lastname character varying, mobilenumber character varying, aadharcardnumber character varying, photopath character varying, username character varying, isactive boolean, rolee text)
    LANGUAGE plpgsql
    AS $$
begin
	return query
	select
	u.id as id,
	u.first_name as firstName,
	u.last_name as lastName, 
	u.mobile_number as mobileNumber, 
	u.aadhar_card_number as aadharCardNumber, 
	u.photo_path as photoPath, 
	u.username as userName, 
	u.is_active as isActive,
	json_build_object(
		'id', r.id, 
		'role', r.role
	)::text as rolee
	from 
	mt_users u 
	left join mt_roles r on u.role_id = r.id
	where u.is_delete = false and u.id = userId;
end;
$$;


ALTER FUNCTION public.fn_get_user_by_id(userid bigint) OWNER TO postgres;

--
-- Name: fn_get_user_role_by_username(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_user_role_by_username(p_username text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
    user_role TEXT;
BEGIN
    SELECT role
    INTO user_role
    FROM users
    WHERE username = p_username;

    RETURN user_role;
END;
$$;


ALTER FUNCTION public.fn_get_user_role_by_username(p_username text) OWNER TO postgres;

--
-- Name: fn_get_users_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_users_list(userid bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(srno bigint, id bigint, namee text, mobilenumber character varying, username character varying, isactive boolean, rolee character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY u.id) AS srNo,
        u.id,
        CONCAT(u.first_name, ' ', u.last_name) AS namee,
        u.mobile_number,
        u.username,
        u.is_active,
        r.role
    FROM mt_users u
    LEFT JOIN mt_roles r ON u.role_id = r.id
    WHERE u.is_delete = false
      AND (userId IS NULL OR u.id = userId)
      AND (searchString IS NULL OR CONCAT(u.first_name, ' ', u.last_name) ILIKE '%' || searchString || '%')
    ORDER BY u.id
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_get_users_list(userid bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_get_users_list_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_users_list_count(userid bigint, searchstring character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    totalListCount integer;
BEGIN
    SELECT COUNT(*) INTO totalListCount
    FROM mt_users u
    LEFT JOIN mt_roles r ON u.role_id = r.id
    WHERE u.is_delete = false
      AND (userId IS NULL OR u.id = userId)
      AND (searchString IS NULL OR CONCAT(u.first_name, ' ', u.last_name) ILIKE '%' || searchString || '%');

    RETURN totalListCount;
END;
$$;


ALTER FUNCTION public.fn_get_users_list_count(userid bigint, searchstring character varying) OWNER TO postgres;

--
-- Name: fn_get_users_point_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_users_point_count(userid bigint, searchstring character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    totalPointCount integer;
BEGIN
    SELECT COUNT(*) INTO totalPointCount
    FROM mt_agency_points p
    WHERE p.is_delete = false
      AND (pointId IS NULL OR p.id = userId)
      AND (searchString IS NULL OR p.point_name ILIKE '%' || searchString || '%');

    RETURN totalPointCount;
END;
$$;


ALTER FUNCTION public.fn_get_users_point_count(userid bigint, searchstring character varying) OWNER TO postgres;

--
-- Name: fn_get_vehicle_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_vehicle_list(p_vehicleid bigint DEFAULT NULL::bigint, p_searchstring character varying DEFAULT NULL::character varying, p_page integer DEFAULT 0, p_size integer DEFAULT 10) RETURNS TABLE(sr_no bigint, vehicle_id bigint, users_name character varying, user_role character varying, vehicle_number character varying, vehicle_type character varying, vehicle_model character varying, fuel_type character varying, load_capacity double precision, last_service_date date, next_service_due date)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY v.id)::bigint AS sr_no,
        v.id::bigint AS vehicle_id,
        COALESCE(u.first_name || ' ' || u.last_name)::character varying AS users_name,
        r.role::character varying AS user_role,
        v.vehicle_number::character varying,
        v.vehicle_type::character varying,
        v.vehicle_model::character varying,
        v.fuel_type::character varying,
        v.load_capacity::double precision,
        v.last_service_date::date,
        v.next_service_due_date::date
    FROM vehicle v
   LEFT JOIN mt_users u ON v.users_id = u.id
LEFT JOIN mt_roles r ON u.role_id = r.id
WHERE v.is_removed = false
  AND (u.id IS NULL OR (u.is_active = true AND u.is_delete = false))
    ORDER BY v.id
    OFFSET (p_page * p_size)
    LIMIT p_size;
END;
$$;


ALTER FUNCTION public.fn_get_vehicle_list(p_vehicleid bigint, p_searchstring character varying, p_page integer, p_size integer) OWNER TO postgres;

--
-- Name: fn_get_vehicles_due_for_service(date, date); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_get_vehicles_due_for_service(from_date date, to_date date) RETURNS TABLE(sr_no bigint, vehicle_id bigint, vehicle_number character varying, vehicle_type character varying, vehicle_model character varying, fuel_type character varying, load_capacity double precision, last_service_date date, next_service_due date)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER (ORDER BY v.next_service_due_date) AS sr_no,
        v.id AS vehicle_id,
        v.vehicle_number,
        v.vehicle_type,
        v.vehicle_model,
        v.fuel_type,
        v.load_capacity,
        v.last_service_date,
        v.next_service_due_date AS next_service_due
    FROM vehicle v
    WHERE 
        v.is_removed = false
        AND (from_date IS NULL OR v.next_service_due_date >= from_date)
        AND (to_date IS NULL OR v.next_service_due_date <= to_date);
END;
$$;


ALTER FUNCTION public.fn_get_vehicles_due_for_service(from_date date, to_date date) OWNER TO postgres;

--
-- Name: fn_mt_get_account_by_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_mt_get_account_by_id(account_id bigint) RETURNS TABLE(id bigint, account_holder_name character varying, bank_name character varying, account_number character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        b.id,
        b.account_holder_name,
        b.bank_name,
        b.account_number,
        b.is_active
    FROM mt_bank_accounts b
    WHERE b.id = account_id;
END;
$$;


ALTER FUNCTION public.fn_mt_get_account_by_id(account_id bigint) OWNER TO postgres;

--
-- Name: fn_mt_get_bank_account_list(bigint, character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_mt_get_bank_account_list(account_id bigint, searchstring character varying, page integer, size integer) RETURNS TABLE(sr_no bigint, id bigint, account_holder_name character varying, bank_name character varying, account_number character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        ROW_NUMBER() OVER (ORDER BY b.id) + (page * size) AS sr_no,
        b.id,
        b.account_holder_name,
        b.bank_name,
        b.account_number,
        b.is_active
    FROM mt_bank_accounts b
    WHERE 
        (account_id IS NULL OR b.id = account_id)
        AND (searchString IS NULL OR 
             LOWER(b.account_holder_name) LIKE LOWER('%' || searchString || '%') OR 
             LOWER(b.bank_name) LIKE LOWER('%' || searchString || '%'))
        AND b.is_delete = false
    ORDER BY b.id
    OFFSET page * size
    LIMIT size;
END;
$$;


ALTER FUNCTION public.fn_mt_get_bank_account_list(account_id bigint, searchstring character varying, page integer, size integer) OWNER TO postgres;

--
-- Name: fn_mt_get_bank_account_list_count(bigint, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_mt_get_bank_account_list_count(account_id bigint, searchstring character varying) RETURNS bigint
    LANGUAGE plpgsql
    AS $$
DECLARE
	total_count bigint;
BEGIN
    SELECT COUNT(*)
    INTO total_count
    FROM mt_bank_accounts b
    WHERE 
        (account_id IS NULL OR b.id = account_id)
        AND (searchString IS NULL OR 
             LOWER(b.account_holder_name) LIKE LOWER('%' || searchString || '%') OR 
             LOWER(b.bank_name) LIKE LOWER('%' || searchString || '%'))
        AND b.is_delete = false;

    RETURN total_count;
END;
$$;


ALTER FUNCTION public.fn_mt_get_bank_account_list_count(account_id bigint, searchstring character varying) OWNER TO postgres;

--
-- Name: fn_product_category_by_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fn_product_category_by_id(productcategoryid bigint) RETURNS TABLE(id bigint, category_name character varying, description character varying, is_active boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        pc.id,
        pc.category_name,
        pc.description,
        pc.is_active
    FROM mt_product_category pc
    WHERE pc.id = productCategoryId
    AND pc.is_delete = false;
END;
$$;


ALTER FUNCTION public.fn_product_category_by_id(productcategoryid bigint) OWNER TO postgres;

--
-- Name: get_daily_assignment_report(text, date, date); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_daily_assignment_report(search_string text, start_date date, end_date date) RETURNS TABLE(assignment_id bigint, delivery_person_name text, assigned_by_name text, product_name text, category_name text, quantity integer, unit_price numeric, created_date timestamp without time zone)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        da.id AS assignment_id,
        CONCAT(dp.first_name, ' ', dp.last_name) AS delivery_person_name,
        CONCAT(ab.first_name, ' ', ab.last_name) AS assigned_by_name,
        p.product_name AS product_name,
        pc.category_name AS category_name,
        dad.quantity_assigned AS quantity,
        dad.unit_price,
        da.created_date
    FROM daily_assignment da
    JOIN mt_users dp ON da.delivery_person_id = dp.id
    JOIN mt_users ab ON da.assigned_by_id = ab.id
    JOIN daily_assignment_details dad ON dad."DailyAssignment" = da.id
    JOIN mt_products p ON dad.product_id = p.id
    JOIN mt_product_category pc ON dad.product_category_id = pc.id
    WHERE 
        (search_string IS NULL OR
         LOWER(dp.first_name) LIKE LOWER(CONCAT('%', search_string, '%')) 
         OR LOWER(dp.last_name) LIKE LOWER(CONCAT('%', search_string, '%'))
         OR LOWER(ab.first_name) LIKE LOWER(CONCAT('%', search_string, '%')) 
         OR LOWER(ab.last_name) LIKE LOWER(CONCAT('%', search_string, '%')))
        AND da.created_date BETWEEN start_date AND end_date
        AND da.is_delete = FALSE;
END;
$$;


ALTER FUNCTION public.get_daily_assignment_report(search_string text, start_date date, end_date date) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: daily_assignment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.daily_assignment (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    is_completed_by_delivery_person boolean,
    is_customer boolean,
    is_delete boolean,
    is_point boolean,
    agency_point_id bigint,
    assigned_by_id bigint,
    customer_id bigint,
    delivery_person_id bigint,
    status_id bigint
);


ALTER TABLE public.daily_assignment OWNER TO postgres;

--
-- Name: daily_assignment_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.daily_assignment_details (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    quantity_assigned integer,
    unit_price double precision,
    daily_assignment bigint,
    product_category_id bigint,
    product_id bigint
);


ALTER TABLE public.daily_assignment_details OWNER TO postgres;

--
-- Name: daily_assignment_details_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.daily_assignment_details ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.daily_assignment_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: daily_assignment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.daily_assignment ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.daily_assignment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: daily_delivery; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.daily_delivery (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    balance_amount double precision,
    cash_amount double precision,
    is_balance boolean,
    is_cash boolean,
    is_customer boolean,
    is_delete boolean,
    is_online boolean,
    is_point boolean,
    online_amount double precision,
    online_photo_path character varying(255),
    quantity integer,
    unfilled_collect_quantity integer,
    point_id bigint,
    bank_account_id bigint,
    customer_id bigint,
    daily_assignment_id bigint,
    delivery_person_id bigint,
    product_id bigint,
    status_id bigint
);


ALTER TABLE public.daily_delivery OWNER TO postgres;

--
-- Name: daily_delivery_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.daily_delivery ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.daily_delivery_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: delivery_exchange_record; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.delivery_exchange_record (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    date date,
    filled_delivered integer,
    filled_returned integer,
    remarks character varying(255),
    unfilled_pending integer,
    unfilled_received integer,
    delivery_boy_id bigint,
    product_id bigint,
    product_category_id bigint
);


ALTER TABLE public.delivery_exchange_record OWNER TO postgres;

--
-- Name: delivery_exchange_record_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.delivery_exchange_record ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.delivery_exchange_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: delivery_person_closer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.delivery_person_closer (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    is_delete boolean,
    total_assigned_cylinder integer,
    total_balance double precision,
    total_cash double precision,
    total_online double precision,
    total_return_cylinder integer,
    total_sale_cylinder integer,
    daily_assignment_id bigint
);


ALTER TABLE public.delivery_person_closer OWNER TO postgres;

--
-- Name: delivery_person_closer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.delivery_person_closer ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.delivery_person_closer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: delivery_person_daily_closer_confirmation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.delivery_person_daily_closer_confirmation (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    confirmed_by_id bigint,
    daily_person_closer_id bigint,
    status_id bigint
);


ALTER TABLE public.delivery_person_daily_closer_confirmation OWNER TO postgres;

--
-- Name: delivery_person_daily_closer_confirmation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.delivery_person_daily_closer_confirmation ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.delivery_person_daily_closer_confirmation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: end_of_day_confirmation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.end_of_day_confirmation (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    is_delete boolean,
    total_assigned_cylinder integer,
    total_balance double precision,
    total_cash double precision,
    total_online double precision,
    total_return_cylinder double precision,
    total_sale_cylinder integer,
    confirmed_by_id bigint,
    send_by_id bigint,
    status_id bigint
);


ALTER TABLE public.end_of_day_confirmation OWNER TO postgres;

--
-- Name: end_of_day_confirmation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.end_of_day_confirmation ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.end_of_day_confirmation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: inventory_stocks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventory_stocks (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    filled_tank integer,
    is_active boolean,
    is_added boolean,
    is_delete boolean,
    is_new_connection boolean,
    is_removed boolean,
    reason character varying(255),
    total_quantity integer,
    un_filled_tank integer,
    unit_price double precision,
    product_category_id bigint,
    product_id bigint
);


ALTER TABLE public.inventory_stocks OWNER TO postgres;

--
-- Name: inventory_stocks_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.inventory_stocks ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.inventory_stocks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: live_inventory_stocks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.live_inventory_stocks (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    filled_tank integer,
    is_active boolean,
    is_delete boolean,
    total_quantity integer,
    un_filled_tank integer,
    product_category_id bigint,
    product_id bigint
);


ALTER TABLE public.live_inventory_stocks OWNER TO postgres;

--
-- Name: live_inventory_stocks_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.live_inventory_stocks ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.live_inventory_stocks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_agency_points; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_agency_points (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    address character varying(255),
    is_active boolean,
    is_delete boolean,
    mobile_number character varying(255),
    point_holder_name character varying(255),
    point_name character varying(255)
);


ALTER TABLE public.mt_agency_points OWNER TO postgres;

--
-- Name: mt_agency_points_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_agency_points ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_agency_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_bank_accounts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_bank_accounts (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    account_holder_name character varying(255),
    account_number character varying(255),
    bank_name character varying(255),
    is_active boolean,
    is_delete boolean
);


ALTER TABLE public.mt_bank_accounts OWNER TO postgres;

--
-- Name: mt_bank_accounts_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_bank_accounts ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_bank_accounts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_customers (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    address character varying(255),
    customer_name character varying(255),
    is_active boolean,
    is_delete boolean,
    mobile_number character varying(255)
);


ALTER TABLE public.mt_customers OWNER TO postgres;

--
-- Name: mt_customers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_customers ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_customers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_new_connections_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_new_connections_details (
    id bigint NOT NULL,
    is_delete boolean,
    quantity integer,
    unit_price double precision NOT NULL,
    new_connection_id bigint NOT NULL,
    product_id bigint NOT NULL
);


ALTER TABLE public.mt_new_connections_details OWNER TO postgres;

--
-- Name: mt_new_connections_details_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_new_connections_details ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_new_connections_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_product_category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_product_category (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    category_name character varying(255) NOT NULL,
    description character varying(255),
    is_active boolean,
    is_delete boolean
);


ALTER TABLE public.mt_product_category OWNER TO postgres;

--
-- Name: mt_product_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_product_category ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_product_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_products (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    is_active boolean,
    is_delete boolean,
    product_price double precision,
    product_name character varying(255) NOT NULL,
    product_category_id bigint NOT NULL
);


ALTER TABLE public.mt_products OWNER TO postgres;

--
-- Name: mt_products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_products ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_roles (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    is_active boolean,
    is_delete boolean,
    role character varying(255) NOT NULL
);


ALTER TABLE public.mt_roles OWNER TO postgres;

--
-- Name: mt_roles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_roles ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_service_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_service_types (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    description character varying(255),
    is_active boolean,
    is_delete boolean,
    service_name character varying(255),
    service_rate double precision
);


ALTER TABLE public.mt_service_types OWNER TO postgres;

--
-- Name: mt_service_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_service_types ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_service_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_status; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_status (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    is_active boolean,
    is_delete boolean,
    status character varying(255)
);


ALTER TABLE public.mt_status OWNER TO postgres;

--
-- Name: mt_status_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_status ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: mt_users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mt_users (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    aadhar_card_number character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    is_active boolean,
    is_delete boolean,
    last_name character varying(255) NOT NULL,
    mobile_number character varying(10) NOT NULL,
    password character varying(255) NOT NULL,
    photo_path character varying(255),
    username character varying(255) NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.mt_users OWNER TO postgres;

--
-- Name: mt_users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mt_users ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.mt_users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: new_connection; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.new_connection (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    cash_amount double precision,
    is_cash boolean,
    is_dbc boolean,
    is_delete boolean,
    is_inventory_buy boolean,
    is_new_connection boolean,
    is_online boolean,
    online_amount double precision,
    online_photo_path character varying(255),
    bank_account_id bigint,
    customer_id bigint
);


ALTER TABLE public.new_connection OWNER TO postgres;

--
-- Name: new_connection_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.new_connection ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.new_connection_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: vehicle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehicle (
    id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    last_modified_by bigint NOT NULL,
    last_modified_date timestamp(6) without time zone NOT NULL,
    fuel_type character varying(255),
    last_service_date date,
    load_capacity double precision,
    next_service_due_date date,
    vehicle_model character varying(255),
    vehicle_number character varying(255),
    vehicle_type character varying(255),
    users_id bigint,
    is_added boolean,
    is_removed boolean,
    is_service_done_on_due boolean,
    CONSTRAINT vehicle_fuel_type_check CHECK (((fuel_type)::text = ANY (ARRAY[('PETROL'::character varying)::text, ('DIESEL'::character varying)::text, ('CNG'::character varying)::text])))
);


ALTER TABLE public.vehicle OWNER TO postgres;

--
-- Name: vehicle_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.vehicle ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.vehicle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: vehicle_service_record; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehicle_service_record (
    id bigint NOT NULL,
    vehicle_service_desc character varying(255),
    due_date_at_time_of_service date,
    is_serviced_on_due_date boolean,
    serviced_location character varying(255),
    odo_meter_reading integer,
    vehicle_service_cost double precision,
    vehicle_service_date date,
    vehicle_serviced_by character varying(255),
    vehicle_id bigint
);


ALTER TABLE public.vehicle_service_record OWNER TO postgres;

--
-- Name: vehicle_service_record_id_seq1; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.vehicle_service_record ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.vehicle_service_record_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Data for Name: daily_assignment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.daily_assignment (id, created_by, created_date, last_modified_by, last_modified_date, is_completed_by_delivery_person, is_customer, is_delete, is_point, agency_point_id, assigned_by_id, customer_id, delivery_person_id, status_id) FROM stdin;
28	45	2025-05-30 15:46:38.45899	45	2025-05-30 15:46:38.45591	t	t	f	f	\N	45	5	59	10
24	45	2025-05-30 11:51:31.329439	45	2025-05-30 11:51:31.321043	t	t	f	f	\N	45	7	60	10
16	45	2025-05-22 16:36:01.381708	45	2025-05-22 16:36:01.371147	t	t	f	\N	\N	45	1	60	3
25	45	2025-05-30 12:27:01.887916	45	2025-05-30 12:27:01.880873	t	t	f	f	\N	45	4	60	10
32	45	2025-05-31 11:31:09.827016	45	2025-05-31 11:31:09.821057	t	t	f	\N	\N	45	8	60	10
23	45	2025-05-28 14:17:08.787169	45	2025-05-28 14:17:08.780163	f	t	t	f	\N	45	7	60	8
12	45	2025-05-20 15:35:42.020295	45	2025-05-20 15:35:42.011838	t	t	f	\N	\N	45	6	38	7
8	45	2025-05-20 12:31:58.748058	45	2025-06-10 12:47:27.7799	t	t	t	\N	\N	45	6	59	3
27	45	2025-05-30 15:25:26.976552	45	2025-05-30 15:25:26.973022	t	t	f	f	\N	45	6	60	10
21	45	2025-05-27 16:54:41.481599	45	2025-05-27 16:54:41.468359	f	t	f	f	\N	45	6	38	9
13	45	2025-05-20 16:15:59.441363	45	2025-05-20 16:15:59.432405	t	t	f	\N	\N	45	4	32	7
33	45	2025-06-02 12:39:43.652188	45	2025-06-02 12:39:43.646135	t	t	f	f	\N	45	5	60	10
39	45	2025-06-10 11:35:20.490668	45	2025-06-10 12:51:14.984548	t	t	f	f	\N	45	6	60	10
2	45	2025-05-20 12:21:09.281938	45	2025-05-20 12:21:09.27892	t	t	f	f	\N	41	3	38	10
18	45	2025-05-23 16:02:47.709623	45	2025-05-23 16:02:47.703398	t	f	f	t	5	45	\N	59	3
34	45	2025-06-02 14:12:03.679269	45	2025-06-02 14:12:03.677075	t	t	f	f	\N	45	8	60	10
22	45	2025-05-28 11:56:00.680505	45	2025-05-28 11:56:00.676419	t	t	f	f	\N	45	7	59	3
6	45	2025-05-20 12:30:07.716713	45	2025-05-20 12:30:07.71073	t	t	f	\N	\N	45	2	59	3
9	45	2025-05-20 12:32:33.862709	45	2025-05-20 12:32:33.855848	t	t	f	\N	\N	45	5	47	3
29	45	2025-05-30 16:02:02.167637	45	2025-05-30 16:02:02.166618	t	t	f	f	\N	45	5	60	10
1	45	2025-05-20 12:20:30.431384	45	2025-05-20 12:20:30.429333	t	t	f	f	\N	45	1	60	3
3	45	2025-05-20 12:21:32.675403	45	2025-05-20 12:21:32.673353	t	t	f	f	\N	45	2	38	10
17	45	2025-05-23 16:01:33.025643	45	2025-05-23 16:01:33.016293	t	t	t	\N	\N	43	7	38	3
15	45	2025-05-20 16:17:25.380199	45	2025-05-20 16:17:25.374186	t	t	f	f	\N	45	4	38	3
4	45	2025-05-20 12:21:58.248933	45	2025-05-20 12:21:58.24248	t	t	f	f	\N	45	4	32	10
19	45	2025-05-26 12:51:26.8178	45	2025-05-26 12:51:26.813496	t	t	f	f	\N	45	5	46	3
10	41	2025-05-20 13:06:46.084835	41	2025-06-10 12:56:24.954896	t	t	f	f	\N	45	6	59	10
11	45	2025-05-20 15:34:21.52163	45	2025-05-20 15:34:21.507631	t	t	f	\N	\N	43	5	44	3
35	45	2025-06-02 14:37:15.551383	45	2025-06-02 14:37:15.548429	t	t	f	f	\N	45	7	60	10
36	45	2025-06-02 18:55:30.389753	45	2025-06-02 18:55:30.385291	f	t	t	f	\N	45	\N	\N	8
31	45	2025-05-30 16:10:56.47781	45	2025-05-30 16:10:56.474759	t	t	f	f	\N	45	4	59	10
30	45	2025-05-30 16:05:16.874396	45	2025-05-30 16:05:16.869365	t	t	f	f	\N	45	6	59	10
26	45	2025-05-30 13:58:13.035648	45	2025-05-30 13:58:13.029653	t	t	f	f	\N	45	5	60	10
20	45	2025-05-26 13:05:36.794027	45	2025-06-10 13:45:53.828258	t	t	f	f	\N	45	6	59	10
37	45	2025-06-02 18:56:11.034959	45	2025-06-02 18:56:11.032951	t	t	f	f	\N	45	9	60	10
38	45	2025-06-04 15:21:49.300103	45	2025-06-04 15:21:49.298149	f	t	f	f	\N	45	5	60	6
5	45	2025-05-20 12:29:22.424239	45	2025-05-20 12:29:22.415257	t	t	f	\N	\N	45	1	59	10
40	45	2025-06-21 20:35:57.268343	45	2025-06-21 20:35:57.251347	f	t	f	f	\N	45	5	\N	8
7	45	2025-05-20 12:31:38.392633	45	2025-05-20 12:31:38.385028	t	t	f	\N	\N	43	3	38	10
42	45	2025-06-21 20:44:38.111818	45	2025-06-21 20:45:02.473758	f	t	f	f	\N	45	10	60	8
43	45	2025-06-21 20:48:30.478585	45	2025-06-21 20:49:07.569144	f	t	f	\N	\N	45	11	38	8
41	45	2025-06-21 20:38:23.601831	45	2025-06-21 20:49:17.956957	f	t	f	\N	\N	45	10	38	8
44	45	2025-07-15 12:34:57.007854	45	2025-07-15 12:35:14.275281	f	t	t	f	\N	45	5	38	8
46	45	2025-07-22 14:57:49.798647	45	2025-07-22 14:57:49.786648	f	t	f	\N	\N	43	1	\N	8
45	45	2025-07-22 14:57:49.691651	45	2025-07-22 14:58:44.320137	f	t	f	\N	\N	45	1	38	8
47	45	2025-08-19 22:47:09.418768	45	2025-08-19 22:49:56.322351	f	t	f	\N	\N	45	12	47	8
48	45	2025-08-19 22:56:10.576979	45	2025-08-19 22:56:10.562987	f	t	f	\N	\N	43	12	\N	8
\.


--
-- Data for Name: daily_assignment_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.daily_assignment_details (id, created_by, created_date, last_modified_by, last_modified_date, quantity_assigned, unit_price, daily_assignment, product_category_id, product_id) FROM stdin;
1	45	2025-05-20 12:20:30.477927	45	2025-05-20 12:20:30.477927	3	500	1	1	1
2	45	2025-05-20 12:21:09.324038	45	2025-05-20 12:21:09.324038	3	1400	2	1	2
3	45	2025-05-20 12:21:32.715461	45	2025-05-20 12:21:32.715461	7	500	3	3	9
4	45	2025-05-20 12:21:58.291448	45	2025-05-20 12:21:58.291448	10	120	4	2	5
5	45	2025-05-20 12:21:58.306931	45	2025-05-20 12:21:58.306931	1	500	4	1	1
6	45	2025-05-20 12:29:22.430228	45	2025-05-20 12:29:22.430228	2	150	5	3	7
7	45	2025-05-20 12:30:07.722723	45	2025-05-20 12:30:07.722723	1	1400	6	1	2
8	45	2025-05-20 12:30:07.729698	45	2025-05-20 12:30:07.729698	1	250	6	3	8
9	45	2025-05-20 12:31:38.398903	45	2025-05-20 12:31:38.398903	2	500	7	3	9
10	45	2025-05-20 12:31:58.753992	45	2025-05-20 12:31:58.753992	1	3000	8	1	3
11	45	2025-05-20 12:32:33.867779	45	2025-05-20 12:32:33.867779	1	3000	9	1	3
12	41	2025-05-20 13:06:46.158887	41	2025-05-20 13:06:46.158887	5	250	10	3	8
13	45	2025-05-20 15:34:21.530628	45	2025-05-20 15:34:21.530628	10	500	11	1	1
14	45	2025-05-20 15:35:42.026313	45	2025-05-20 15:35:42.026313	9	1400	12	1	2
15	45	2025-05-20 16:15:59.448362	45	2025-05-20 16:15:59.448362	5	500	13	1	1
16	45	2025-05-20 16:15:59.45637	45	2025-05-20 16:15:59.45637	5	120	13	2	5
17	45	2025-05-20 16:17:25.422458	45	2025-05-20 16:17:25.422458	5	250	15	3	8
18	45	2025-05-22 16:36:01.392165	45	2025-05-22 16:36:01.392165	3	15	16	2	4
19	45	2025-05-23 16:01:33.035627	45	2025-05-23 16:01:33.035627	1	1400	17	1	2
20	45	2025-05-23 16:02:47.721587	45	2025-05-23 16:02:47.721587	9	1400	18	1	2
21	45	2025-05-26 12:51:26.829423	45	2025-05-26 12:51:26.829423	1	1400	19	1	2
22	45	2025-05-26 12:51:26.869551	45	2025-05-26 12:51:26.869551	1	500	19	3	9
23	45	2025-05-26 13:05:36.929315	45	2025-05-26 13:05:36.929315	1	1400	20	1	2
24	45	2025-05-26 13:05:36.998912	45	2025-05-26 13:05:36.998912	1	200	20	2	6
25	45	2025-05-26 13:05:37.026469	45	2025-05-26 13:05:37.026469	1	15	20	2	4
26	45	2025-05-27 16:54:41.50257	45	2025-05-27 16:54:41.50257	11	150	21	3	7
27	45	2025-05-28 11:56:00.831224	45	2025-05-28 11:56:00.831224	2	1400	22	1	2
28	45	2025-05-28 11:56:00.906453	45	2025-05-28 11:56:00.906453	1	3000	22	1	3
29	45	2025-05-28 14:17:08.833265	45	2025-05-28 14:17:08.833265	1	120	23	2	5
30	45	2025-05-28 14:17:08.86927	45	2025-05-28 14:17:08.86927	1	500	23	3	9
31	45	2025-05-30 11:51:31.490011	45	2025-05-30 11:51:31.490011	1	1400	24	1	2
32	45	2025-05-30 11:51:31.584713	45	2025-05-30 11:51:31.584713	1	3000	24	1	3
33	45	2025-05-30 11:51:31.616716	45	2025-05-30 11:51:31.616716	1	500	24	1	1
34	45	2025-05-30 12:27:01.930749	45	2025-05-30 12:27:01.930749	1	500	25	1	1
35	45	2025-05-30 13:58:13.164087	45	2025-05-30 13:58:13.164087	1	500	26	3	9
36	45	2025-05-30 15:25:27.015231	45	2025-05-30 15:25:27.015231	1	500	27	3	9
37	45	2025-05-30 15:25:27.051355	45	2025-05-30 15:25:27.051355	1	3000	27	1	3
38	45	2025-05-30 15:46:38.482487	45	2025-05-30 15:46:38.482487	1	1400	28	1	2
39	45	2025-05-30 16:02:02.203868	45	2025-05-30 16:02:02.203868	1	250	29	3	8
40	45	2025-05-30 16:05:16.914505	45	2025-05-30 16:05:16.914505	1	3000	30	1	3
41	45	2025-05-30 16:10:56.493468	45	2025-05-30 16:10:56.493468	1	500	31	3	9
42	45	2025-05-31 11:31:09.837018	45	2025-05-31 11:31:09.837018	1	1400	32	1	2
43	45	2025-06-02 12:39:43.663086	45	2025-06-02 12:39:43.663086	10	1400	33	1	2
44	45	2025-06-02 14:12:03.736585	45	2025-06-02 14:12:03.736585	1	1400	34	1	2
45	45	2025-06-02 14:37:15.597964	45	2025-06-02 14:37:15.597964	1	150	35	3	7
46	45	2025-06-02 14:37:15.635003	45	2025-06-02 14:37:15.635003	1	15	35	2	4
47	45	2025-06-02 14:37:15.650004	45	2025-06-02 14:37:15.650004	1	1400	35	1	2
48	45	2025-06-02 18:55:30.435928	45	2025-06-02 18:55:30.435928	1	1400	36	1	2
49	45	2025-06-02 18:56:11.069742	45	2025-06-02 18:56:11.069742	1	1400	37	1	2
50	45	2025-06-02 18:56:11.086884	45	2025-06-02 18:56:11.086884	1	120	37	2	5
51	45	2025-06-04 15:21:49.380917	45	2025-06-04 15:21:49.380917	10	3000	38	1	3
52	45	2025-06-10 11:35:20.535852	45	2025-06-10 11:35:20.535852	10	15	39	2	4
53	45	2025-06-21 20:35:57.644114	45	2025-06-21 20:35:57.644114	1	200	40	2	6
54	45	2025-06-21 20:38:23.612828	45	2025-06-21 20:38:23.612828	1	120	41	2	5
55	45	2025-06-21 20:44:38.132421	45	2025-06-21 20:44:38.132421	3	3000	42	1	3
56	45	2025-06-21 20:44:38.179425	45	2025-06-21 20:44:38.179425	1	200	42	2	6
57	45	2025-06-21 20:48:30.492581	45	2025-06-21 20:48:30.492581	1	1400	43	1	2
58	45	2025-07-15 12:34:57.618544	45	2025-07-15 12:34:57.618544	1	500	44	3	9
59	45	2025-07-15 12:34:57.765721	45	2025-07-15 12:34:57.765721	1	500	44	1	1
60	45	2025-07-22 14:57:49.738649	45	2025-07-22 14:57:49.738649	1	120	45	2	5
61	45	2025-07-22 14:57:49.805645	45	2025-07-22 14:57:49.805645	1	120	46	2	5
62	45	2025-08-19 22:47:09.436765	45	2025-08-19 22:47:09.436765	1	3000	47	1	3
63	45	2025-08-19 22:47:09.454761	45	2025-08-19 22:47:09.454761	3	120	47	2	5
64	45	2025-08-19 22:56:10.58898	45	2025-08-19 22:56:10.58898	1	1400	48	1	2
\.


--
-- Data for Name: daily_delivery; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.daily_delivery (id, created_by, created_date, last_modified_by, last_modified_date, balance_amount, cash_amount, is_balance, is_cash, is_customer, is_delete, is_online, is_point, online_amount, online_photo_path, quantity, unfilled_collect_quantity, point_id, bank_account_id, customer_id, daily_assignment_id, delivery_person_id, product_id, status_id) FROM stdin;
4	41	2025-05-21 11:23:19.039817	41	2025-05-21 11:23:19.039817	0	1000	f	t	t	f	f	f	0	\N	5	0	\N	\N	4	4	38	8	3
5	41	2025-05-21 12:08:41.16531	41	2025-05-21 12:08:41.166291	0	1000	f	t	t	t	f	f	\N	\N	3	3	\N	\N	1	1	38	1	3
6	41	2025-05-21 12:55:31.049788	41	2025-05-21 12:55:31.049788	0	1000	f	t	t	t	f	f	\N	\N	10	10	\N	\N	6	13	38	1	3
7	41	2025-05-21 14:08:21.570825	41	2025-05-21 14:08:21.571755	0	1000	f	t	t	t	f	f	\N	\N	2	0	\N	\N	3	7	38	2	3
13	41	2025-05-21 17:54:19.492843	41	2025-05-21 17:54:19.492843	200	1000	t	t	t	f	f	f	\N	\N	1	1	\N	\N	6	8	38	3	3
14	41	2025-05-23 11:04:01.414044	41	2025-05-23 11:04:01.414044	0	100	f	t	t	t	f	f	0	\N	1	1	\N	\N	5	9	47	3	3
16	41	2025-05-26 12:33:54.513987	41	2025-05-26 12:33:54.513987	\N	1000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	4	15	38	8	3
17	41	2025-05-26 12:52:42.844254	41	2025-05-26 12:52:42.844254	\N	1500	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	5	19	46	9	3
15	41	2025-05-26 12:16:58.219696	41	2025-05-26 12:16:58.219696	\N	1000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	17	38	2	3
18	41	2025-05-26 16:38:46.26689	41	2025-05-26 16:38:46.26689	\N	1000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	5	11	44	1	3
19	41	2025-05-27 12:14:38.802363	41	2025-05-27 12:14:38.802363	\N	1000	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	2	6	59	2	3
20	41	2025-05-27 12:14:38.977608	41	2025-05-27 12:14:38.977608	\N	1000	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	2	6	59	8	3
21	59	2025-05-27 12:17:04.788294	59	2025-05-27 12:17:04.788294	\N	45	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	1	16	59	4	3
22	59	2025-05-27 12:19:58.368975	59	2025-05-27 12:19:58.368975	\N	3000	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	6	8	59	3	3
23	59	2025-05-27 12:35:41.714245	59	2025-05-27 12:35:41.714245	\N	3000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	20	59	2	3
24	59	2025-05-27 12:35:41.756644	59	2025-05-27 12:35:41.756644	\N	3000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	20	59	6	3
25	59	2025-05-27 12:35:41.762162	59	2025-05-27 12:35:41.762162	\N	3000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	20	59	4	3
26	59	2025-05-28 12:43:10.836995	59	2025-05-28 12:43:10.836995	\N	1000	\N	t	f	f	f	t	\N	\N	\N	\N	5	\N	\N	18	59	2	3
27	59	2025-05-28 12:44:11.451242	59	2025-05-28 12:44:11.451242	\N	12600	\N	t	f	f	f	t	\N	\N	\N	\N	5	\N	\N	18	59	2	3
28	59	2025-05-28 14:19:33.045819	59	2025-05-28 14:19:33.045819	\N	2000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	22	59	2	3
29	59	2025-05-28 14:19:33.062599	59	2025-05-28 14:19:33.062599	\N	2000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	22	59	3	3
30	59	2025-05-30 10:36:29.311925	59	2025-05-30 10:36:29.311925	\N	1650	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	2	6	59	2	3
31	59	2025-05-30 10:36:29.436322	59	2025-05-30 10:36:29.436322	\N	1650	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	2	6	59	8	3
32	60	2025-05-30 10:59:41.174597	60	2025-05-30 10:59:41.174597	\N	1500	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	1	1	60	1	3
33	41	2025-05-30 11:55:31.152319	41	2025-05-30 11:55:31.152319	\N	1000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	24	60	2	3
34	41	2025-05-30 11:55:31.176572	41	2025-05-30 11:55:31.176572	\N	1000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	24	60	3	3
35	41	2025-05-30 11:55:31.191576	41	2025-05-30 11:55:31.191576	\N	1000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	24	60	1	3
36	60	2025-05-30 12:26:33.858558	60	2025-05-30 12:26:33.858558	\N	45	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	1	16	60	4	3
37	60	2025-05-30 12:27:25.970688	60	2025-05-30 12:27:25.970688	\N	500	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	4	25	60	1	3
38	60	2025-05-30 14:00:17.915887	60	2025-05-30 14:00:17.915887	\N	500	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	5	26	60	9	3
39	60	2025-05-30 15:26:29.702812	60	2025-05-30 15:26:29.702812	\N	3500	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	27	60	9	3
40	60	2025-05-30 15:26:29.722981	60	2025-05-30 15:26:29.722981	\N	3500	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	27	60	3	3
41	59	2025-05-30 15:47:50.531123	59	2025-05-30 15:47:50.531123	\N	1400	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	5	28	59	2	3
42	60	2025-05-30 16:02:28.588689	60	2025-05-30 16:02:28.588689	\N	250	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	5	29	60	8	3
43	60	2025-05-30 16:02:28.630858	60	2025-05-30 16:02:28.630858	\N	250	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	5	29	60	8	3
44	59	2025-05-30 16:05:46.675348	59	2025-05-30 16:05:46.675348	\N	3000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	30	59	3	3
45	59	2025-05-30 16:11:26.943349	59	2025-05-30 16:11:26.943349	\N	500	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	4	31	59	9	3
46	60	2025-05-31 11:31:51.864208	60	2025-05-31 11:31:51.864208	\N	1400	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	8	32	60	2	3
47	60	2025-06-02 12:40:25.335482	60	2025-06-02 12:40:25.335482	\N	14000	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	5	33	60	2	3
48	60	2025-06-02 14:13:41.523301	60	2025-06-02 14:13:41.523301	\N	1400	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	8	34	60	2	3
49	60	2025-06-02 14:38:16.388689	60	2025-06-02 14:38:16.388689	\N	1565	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	35	60	7	3
50	60	2025-06-02 14:38:16.401781	60	2025-06-02 14:38:16.401781	\N	1565	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	35	60	4	3
51	60	2025-06-02 14:38:16.410744	60	2025-06-02 14:38:16.410744	\N	1565	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	7	35	60	2	3
52	60	2025-06-02 18:57:38.301971	60	2025-06-02 18:57:38.301971	\N	1520	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	9	37	60	2	3
53	60	2025-06-02 18:57:38.31442	60	2025-06-02 18:57:38.31442	\N	1520	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	9	37	60	5	3
54	60	2025-06-10 12:40:11.411513	60	2025-06-10 12:40:11.411513	\N	3000	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	6	8	60	3	3
55	59	2025-06-10 12:48:24.607812	59	2025-06-10 12:48:24.607812	\N	3000	\N	t	t	f	f	\N	\N	\N	\N	\N	\N	\N	6	8	59	3	3
56	60	2025-06-10 12:51:45.189452	60	2025-06-10 12:51:45.189452	\N	150	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	39	60	4	3
57	59	2025-06-10 13:44:35.719709	59	2025-06-10 13:44:35.719709	\N	1250	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	10	59	8	3
58	59	2025-06-10 13:46:33.979086	59	2025-06-10 13:46:33.979086	\N	1615	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	20	59	2	3
59	59	2025-06-10 13:46:34.035732	59	2025-06-10 13:46:34.035732	\N	1615	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	20	59	6	3
60	59	2025-06-10 13:46:34.05349	59	2025-06-10 13:46:34.05349	\N	1615	\N	t	t	f	f	f	\N	\N	\N	\N	\N	\N	6	20	59	4	3
\.


--
-- Data for Name: delivery_exchange_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.delivery_exchange_record (id, created_by, created_date, last_modified_by, last_modified_date, date, filled_delivered, filled_returned, remarks, unfilled_pending, unfilled_received, delivery_boy_id, product_id, product_category_id) FROM stdin;
\.


--
-- Data for Name: delivery_person_closer; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.delivery_person_closer (id, created_by, created_date, last_modified_by, last_modified_date, is_delete, total_assigned_cylinder, total_balance, total_cash, total_online, total_return_cylinder, total_sale_cylinder, daily_assignment_id) FROM stdin;
1	41	2025-05-20 12:41:46.61464	41	2025-05-20 12:41:46.61464	f	3	0	1000	0	3	3	1
2	41	2025-05-20 13:10:34.295246	41	2025-05-20 13:10:34.295246	f	3	0	1000	0	3	3	2
3	41	2025-05-21 12:18:41.591987	41	2025-05-21 12:18:41.591987	f	2	0	1000	0	2	2	4
4	41	2025-05-21 14:01:15.210466	41	2025-05-21 14:01:15.210466	f	2	0	1000	0	2	2	13
5	41	2025-05-21 15:58:00.1219	41	2025-05-21 15:58:00.1219	f	2	0	1000	0	2	2	7
6	41	2025-05-21 16:56:29.249218	41	2025-05-21 16:56:29.249218	f	1	200	1000	0	1	1	8
7	45	2025-05-30 11:56:42.482497	45	2025-05-30 11:56:42.482497	f	3	0	4900	0	3	3	24
8	45	2025-05-30 12:33:31.94855	45	2025-05-30 12:33:31.94855	f	1	\N	\N	\N	1	1	25
9	45	2025-05-30 15:19:53.600603	45	2025-05-30 15:19:53.600603	f	2	\N	\N	\N	0	0	5
10	45	2025-05-30 15:23:31.383293	45	2025-05-30 15:23:31.383293	f	1	\N	\N	\N	0	0	26
11	45	2025-05-30 15:27:40.446737	45	2025-05-30 15:27:40.446737	f	1	\N	\N	\N	1	1	27
12	45	2025-05-30 15:58:35.385178	45	2025-05-30 15:58:35.385178	f	1	\N	\N	\N	1	1	28
13	45	2025-05-30 16:02:45.692778	45	2025-05-30 16:02:45.692778	f	1	\N	\N	\N	0	0	29
14	45	2025-05-30 16:05:57.327067	45	2025-05-30 16:05:57.327067	f	1	\N	\N	\N	1	1	30
15	45	2025-05-30 16:11:33.381996	45	2025-05-30 16:11:33.381996	f	1	\N	\N	\N	0	0	31
16	45	2025-05-31 11:31:56.323895	45	2025-05-31 11:31:56.323895	f	1	\N	\N	\N	1	1	32
17	45	2025-06-02 12:40:52.865906	45	2025-06-02 12:40:52.865906	f	10	\N	\N	\N	10	10	33
18	45	2025-06-02 14:13:47.962099	45	2025-06-02 14:13:47.962099	f	1	\N	\N	\N	1	1	34
19	45	2025-06-02 14:38:45.028904	45	2025-06-02 14:38:45.028904	f	1	\N	\N	\N	1	1	35
20	45	2025-06-02 18:57:43.396186	45	2025-06-02 18:57:43.396186	f	1	\N	\N	\N	0	0	37
21	45	2025-06-10 12:52:07.013254	45	2025-06-10 12:52:07.013254	f	10	\N	\N	\N	0	0	39
22	41	2025-06-10 13:44:59.026032	41	2025-06-10 13:44:59.026032	f	5	\N	\N	\N	0	0	10
23	45	2025-06-10 13:46:35.988455	45	2025-06-10 13:46:35.988455	f	1	\N	\N	\N	0	0	20
\.


--
-- Data for Name: delivery_person_daily_closer_confirmation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.delivery_person_daily_closer_confirmation (id, created_by, created_date, last_modified_by, last_modified_date, confirmed_by_id, daily_person_closer_id, status_id) FROM stdin;
1	41	2025-05-20 16:58:06.275176	41	2025-05-20 16:58:06.275176	41	2	10
2	41	2025-05-21 17:53:41.113001	41	2025-05-21 17:53:41.113001	45	8	10
3	41	2025-05-30 11:03:35.734088	41	2025-05-30 11:03:35.734088	41	3	10
4	45	2025-05-30 11:19:38.705775	45	2025-05-30 11:19:38.705775	45	4	10
5	45	2025-05-30 12:25:50.350483	45	2025-05-30 12:25:50.350483	45	24	10
6	45	2025-05-30 12:33:46.894297	45	2025-05-30 12:33:46.894297	45	25	10
7	45	2025-05-30 15:28:02.235869	45	2025-05-30 15:28:02.235869	45	27	10
8	45	2025-05-30 16:04:02.349286	45	2025-05-30 16:04:02.349286	45	29	10
9	45	2025-05-30 16:12:52.041315	45	2025-05-30 16:12:52.041315	45	31	10
10	45	2025-05-30 16:13:09.489741	45	2025-05-30 16:13:09.489741	45	30	10
11	45	2025-05-30 16:20:18.666518	45	2025-05-30 16:20:18.666518	45	26	10
12	45	2025-05-30 16:21:08.20124	45	2025-05-30 16:21:08.20124	45	28	10
13	45	2025-06-02 12:37:13.605385	45	2025-06-02 12:37:13.605385	45	32	10
14	45	2025-06-02 12:41:02.585824	45	2025-06-02 12:41:02.585824	45	33	10
15	45	2025-06-02 14:14:09.612362	45	2025-06-02 14:14:09.612362	45	34	10
16	45	2025-06-02 14:38:56.187546	45	2025-06-02 14:38:56.187546	45	35	10
17	45	2025-06-02 18:57:52.037926	45	2025-06-02 18:57:52.037926	45	37	10
18	45	2025-06-10 11:20:06.361952	45	2025-06-10 11:20:06.361952	45	5	10
19	45	2025-06-10 12:52:13.248084	45	2025-06-10 12:52:13.248084	45	39	10
20	41	2025-06-10 13:45:10.186003	41	2025-06-10 13:45:10.186003	45	10	10
21	45	2025-06-10 13:47:16.372547	45	2025-06-10 13:47:16.372547	45	20	10
22	45	2025-06-21 20:36:13.090516	45	2025-06-21 20:36:13.090516	43	7	10
\.


--
-- Data for Name: end_of_day_confirmation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.end_of_day_confirmation (id, created_by, created_date, last_modified_by, last_modified_date, is_delete, total_assigned_cylinder, total_balance, total_cash, total_online, total_return_cylinder, total_sale_cylinder, confirmed_by_id, send_by_id, status_id) FROM stdin;
13	1	2025-04-29 12:10:24.069665	1	2025-04-29 12:10:24.069665	f	4	0	3000	0	4	4	39	37	7
14	1	2025-04-29 12:10:29.346105	1	2025-04-29 12:10:29.346105	f	4	0	3000	0	4	4	39	37	7
15	1	2025-04-29 12:32:11.276275	1	2025-04-29 12:32:11.276275	f	4	0	3000	0	4	4	39	37	7
16	1	2025-04-29 12:32:15.802858	1	2025-04-29 12:32:15.803897	f	4	0	3000	0	4	4	39	37	7
17	1	2025-04-29 12:32:23.516622	1	2025-04-29 12:32:23.516622	f	4	0	3000	0	4	4	39	37	7
18	1	2025-04-29 12:36:21.875049	1	2025-04-29 12:36:21.875049	f	4	0	3000	0	4	4	39	37	7
19	1	2025-04-29 12:36:56.010301	1	2025-04-29 12:36:56.010301	f	4	0	3000	0	4	4	39	37	7
20	1	2025-04-29 12:39:39.351865	1	2025-04-29 12:39:39.351865	f	4	0	3000	0	4	4	39	37	7
21	1	2025-04-29 12:48:01.513459	1	2025-04-29 12:48:01.513459	f	4	0	3000	0	4	4	39	37	10
22	1	2025-04-29 12:51:07.994481	1	2025-04-29 12:51:07.994481	f	4	0	3000	0	4	4	39	37	10
23	1	2025-04-29 12:51:11.318598	1	2025-04-29 12:51:11.318598	f	4	0	3000	0	4	4	39	37	10
24	1	2025-04-29 14:15:25.44385	1	2025-04-29 14:15:25.44385	f	4	0	3000	0	4	4	39	37	10
25	1	2025-04-29 14:15:29.122624	1	2025-04-29 14:15:29.122624	f	4	0	3000	0	4	4	39	37	10
26	1	2025-04-29 14:15:32.788445	1	2025-04-29 14:15:32.788445	f	4	0	3000	0	4	4	39	37	10
27	1	2025-04-29 14:38:07.280326	1	2025-04-29 14:38:07.280326	f	4	0	3000	0	4	4	39	37	10
28	1	2025-04-29 14:38:10.148374	1	2025-04-29 14:38:10.148374	f	4	0	3000	0	4	4	39	37	10
\.


--
-- Data for Name: inventory_stocks; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inventory_stocks (id, created_by, created_date, last_modified_by, last_modified_date, filled_tank, is_active, is_added, is_delete, is_new_connection, is_removed, reason, total_quantity, un_filled_tank, unit_price, product_category_id, product_id) FROM stdin;
1	45	2025-05-20 12:23:49.908368	45	2025-05-20 12:23:49.908368	100	t	t	f	f	f	New Stock added	110	10	500	1	1
2	45	2025-05-20 12:24:16.161754	45	2025-05-20 12:24:16.161754	400	t	t	f	f	f	New Stock added	450	50	1400	1	2
3	45	2025-05-20 12:25:03.98795	45	2025-05-20 12:25:03.98795	500	t	t	f	f	f	New Stock added	600	100	3000	1	3
4	45	2025-05-20 12:26:01.012084	45	2025-05-20 12:26:01.012084	200	t	t	f	f	f	New Stock added	200	0	14.99	2	4
5	45	2025-05-20 12:26:32.967838	45	2025-05-20 12:26:32.967838	300	t	t	f	f	f	New Stock added	300	0	120	2	5
6	45	2025-05-20 12:27:04.523422	45	2025-05-20 12:27:04.523422	200	t	t	f	f	f	New Stock added	200	0	200	2	6
7	45	2025-05-20 12:27:25.714751	45	2025-05-20 12:27:25.714751	400	t	t	f	f	f	New Stock added	400	0	149.98	3	7
8	45	2025-05-20 12:27:54.913319	45	2025-05-20 12:27:54.913319	350	t	t	f	f	f	New Stock added	350	0	249.98	3	8
9	45	2025-05-20 12:28:20.598924	45	2025-05-20 12:28:20.598924	230	t	t	f	f	f	New Stock added	230	0	500	3	9
\.


--
-- Data for Name: live_inventory_stocks; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.live_inventory_stocks (id, created_by, created_date, last_modified_by, last_modified_date, filled_tank, is_active, is_delete, total_quantity, un_filled_tank, product_category_id, product_id) FROM stdin;
8	45	2025-05-20 12:27:54.925319	45	2025-05-20 12:27:54.925319	376	t	f	376	0	3	8
7	45	2025-05-20 12:27:25.752001	45	2025-05-20 12:27:25.752001	401	t	f	401	0	3	7
4	45	2025-05-20 12:26:01.052759	45	2025-05-20 12:26:01.052759	190	t	f	190	0	2	4
6	45	2025-05-20 12:27:04.539625	45	2025-05-20 12:27:04.539625	199	t	f	199	0	2	6
9	45	2025-05-20 12:28:20.644079	45	2025-05-20 12:28:20.644079	224	t	f	224	0	3	9
1	45	2025-05-20 12:23:49.958195	45	2025-05-20 12:23:49.958195	88	t	f	98	10	1	1
3	45	2025-05-20 12:25:04.029405	45	2025-05-20 12:25:04.029405	483	t	f	583	100	1	3
5	45	2025-05-20 12:26:33.010979	45	2025-05-20 12:26:33.010979	287	t	f	287	0	2	5
2	45	2025-05-20 12:24:16.205657	45	2025-05-20 12:24:16.205657	374	t	f	424	50	1	2
\.


--
-- Data for Name: mt_agency_points; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_agency_points (id, created_by, created_date, last_modified_by, last_modified_date, address, is_active, is_delete, mobile_number, point_holder_name, point_name) FROM stdin;
3	2	2025-04-18 14:48:18.637099	2	2025-04-18 14:48:18.637099	Pune	t	f	9876567811	sham sutar	Wakad, D mart
2	2	2025-04-18 14:47:56.124868	2	2025-04-18 14:47:56.124868	Pune	t	t	9876567876	Ram Kadam	Wakad, Near mall
5	45	2025-05-16 11:44:27.487002	45	2025-05-16 11:46:06.204248	Flat 12, Ganga Residency, Aundh, Pune, Maharashtra - 411007	t	f	9123456780	Priya Kulkarni	Kulkarni Bharat Gas Agency
4	45	2025-05-16 11:43:06.006019	45	2025-05-16 11:48:56.855104	Shop No. 5, Sai Complex, Kothrud, Pune, Maharashtra - 411038	f	f	9876543210	Rajesh Shinde	Shinde HP Gas Agency
6	45	2025-05-16 12:55:54.409139	45	2025-05-16 12:56:04.966725	Survey No. 22, Baner Road, Baner, Pune, Maharashtra - 411045	f	f	9988776655	Suresh Jagtap	Jagtap Gas Distributors
7	45	2025-05-19 16:32:30.52489	45	2025-05-19 16:32:30.52489	Shree Nagar, Yerwada, Pune, Maharashtra - 411006	t	f	8899776655	Manisha More	More Indane Gas Agency
1	1	2025-04-17 13:48:09.549315	45	2025-06-21 20:36:36.12029	Balbalgaon	t	f	9090998999	Mr. John Walker	Point - BalbalGaon
\.


--
-- Data for Name: mt_bank_accounts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_bank_accounts (id, created_by, created_date, last_modified_by, last_modified_date, account_holder_name, account_number, bank_name, is_active, is_delete) FROM stdin;
1	1	2025-04-17 13:47:16.344221	1	2025-04-17 13:47:16.344221	Mr. Ganjanan Kharat	989898868686	HDFC Bank	t	f
4	2	2025-04-18 14:51:38.589761	2	2025-04-18 14:51:38.589761	Manik	987654321122	HDFC	t	f
2	1	2025-04-18 14:18:25.148701	1	2025-04-18 14:18:25.148701	Alex	909090909090	HDFC	t	t
5	1	2025-04-21 12:56:55.38848	1	2025-04-21 12:56:55.38848	Pooja	123456789012	SBI	t	f
3	2	2025-04-18 14:51:01.472693	2	2025-04-18 14:51:01.472693	Vinayak Sutar	987654321111	SBI	f	t
\.


--
-- Data for Name: mt_customers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_customers (id, created_by, created_date, last_modified_by, last_modified_date, address, customer_name, is_active, is_delete, mobile_number) FROM stdin;
2	45	2025-05-20 12:18:28.312087	45	2025-05-20 12:18:28.312087	Dighi, Pune	Vinayak Deshmukh	t	f	9090876543
4	45	2025-05-20 12:19:19.012913	45	2025-05-20 12:19:19.012913	Shivajinagar, Pune	Veer H	t	f	9011112234
5	45	2025-05-20 12:19:44.185947	45	2025-05-20 12:19:44.185947	Karve nagar, Pune	Shushma Motekar	t	f	7865432190
6	45	2025-05-20 12:20:04.246064	45	2025-05-20 12:20:04.246064	Kalewadi, Pune	Prajyot Pathade	t	f	6767678900
3	45	2025-05-20 12:18:53.61576	45	2025-05-20 12:33:07.405725	Lakshmi chowk, Pune	Santosh Bichukale	t	f	8765432111
7	45	2025-05-23 16:00:39.813614	45	2025-05-23 16:00:39.813614	Shivaji Nagar, Pune	Alex Desueza	t	f	2323234567
8	45	2025-05-31 11:30:42.179421	45	2025-05-31 11:30:42.179421	Bhumkar, Pune	Ritesh Deshmukh	t	f	8888888888
9	45	2025-06-02 18:55:11.301166	45	2025-06-02 18:55:11.301166	Dange Chowk ,Pune	Sai Deshmukh	t	f	5656565678
1	45	2025-05-20 12:18:07.062071	45	2025-06-21 20:37:05.870119	Wakad, Pune	Pooja Wagh	t	f	8518924684
10	45	2025-06-21 20:37:46.024809	45	2025-06-21 20:37:46.024809	Dhule, Nashik	Alexander	t	f	8787876543
12	45	2025-08-19 22:45:49.026003	45	2025-08-19 22:48:04.993947	Pune, wakad	Om	t	f	8787878787
11	45	2025-06-21 20:47:32.557773	45	2025-06-21 20:47:32.557773	Pune	Sushil Pokharkar	f	t	9518924684
\.


--
-- Data for Name: mt_new_connections_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_new_connections_details (id, is_delete, quantity, unit_price, new_connection_id, product_id) FROM stdin;
1	f	1	1400	1	2
2	f	2	150	2	7
3	f	1	1400	3	2
4	f	1	250	3	8
5	f	2	500	4	9
6	f	1	3000	5	3
7	f	1	3000	6	3
8	f	10	500	7	1
9	f	9	1400	8	2
10	f	5	500	9	1
11	f	5	120	9	5
12	f	3	15	10	4
13	f	1	1400	11	2
14	f	1	1400	12	2
15	f	1	120	13	5
16	f	1	1400	14	2
17	f	1	120	15	5
18	f	1	120	16	5
19	f	1	3000	17	3
20	f	3	120	17	5
21	f	1	1400	18	2
\.


--
-- Data for Name: mt_product_category; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_product_category (id, created_by, created_date, last_modified_by, last_modified_date, category_name, description, is_active, is_delete) FROM stdin;
1	41	2025-05-20 12:01:35.677746	41	2025-05-20 12:01:35.677746	GAS CYLINDER	This is Gas cylinder	t	f
2	41	2025-05-20 12:01:52.290332	41	2025-05-20 12:01:52.290332	LIGHTERS	This is Lighters	t	f
3	41	2025-05-20 12:02:05.281822	41	2025-05-20 12:02:05.281822	BURNERS	This is Burners	t	f
\.


--
-- Data for Name: mt_products; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_products (id, created_by, created_date, last_modified_by, last_modified_date, is_active, is_delete, product_price, product_name, product_category_id) FROM stdin;
1	41	2025-05-20 12:02:23.112757	41	2025-05-20 12:02:23.112757	t	f	500	Gas Cylinder - 5 KG	1
2	41	2025-05-20 12:02:36.208969	41	2025-05-20 12:02:36.208969	t	f	1400	Gas Cylinder - 14 KG	1
3	41	2025-05-20 12:02:45.771267	41	2025-05-20 12:02:45.771267	t	f	3000	Gas Cylinder - 29 KG	1
4	41	2025-05-20 12:03:02.27652	41	2025-05-20 12:03:02.27652	t	f	15	Small Lighter	2
5	41	2025-05-20 12:03:15.360506	41	2025-05-20 12:03:15.360506	t	f	120	Medium size Lighter	2
6	41	2025-05-20 12:03:30.1565	41	2025-05-20 12:03:30.1565	t	f	200	Large Lighter	2
7	41	2025-05-20 12:03:47.80848	41	2025-05-20 12:03:47.80848	t	f	150	Small Burner	3
8	41	2025-05-20 12:03:57.8068	41	2025-05-20 12:03:57.8068	t	f	250	Medium Burner	3
9	41	2025-05-20 12:04:04.960378	41	2025-05-20 12:04:04.960378	t	f	500	Larger Burner	3
\.


--
-- Data for Name: mt_roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_roles (id, created_by, created_date, last_modified_by, last_modified_date, is_active, is_delete, role) FROM stdin;
8	32	2025-04-22 14:31:26.397047	32	2025-04-22 14:31:26.397047	t	f	DeliveryBoy
3	2	2025-04-18 10:54:55.824112	2	2025-04-18 10:54:55.824112	t	f	Manager
1	1	2025-04-17 12:32:05.003969	1	2025-04-17 12:32:05.003969	t	f	Admin
\.


--
-- Data for Name: mt_service_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_service_types (id, created_by, created_date, last_modified_by, last_modified_date, description, is_active, is_delete, service_name, service_rate) FROM stdin;
1	1	2025-04-17 13:48:42.360998	1	2025-04-17 13:48:42.360998	Gas pipe neet kele	t	f	Gas pipe	100
2	2	2025-04-18 14:50:21.032937	2	2025-04-18 14:50:21.033373	Good service	t	t	abc	3
\.


--
-- Data for Name: mt_status; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_status (id, created_by, created_date, last_modified_by, last_modified_date, is_active, is_delete, status) FROM stdin;
6	1	2025-04-24 15:36:29.00176	1	2025-04-24 15:36:29.00176	t	f	IN_PROGRESS
3	1	2025-04-24 15:22:42.040658	1	2025-04-24 15:22:42.040658	t	f	DELIVERED
4	1	2025-04-24 15:23:50.027419	1	2025-04-24 15:23:50.027419	t	f	NOT_DELIVERED
5	1	2025-04-24 15:24:29.049866	1	2025-04-24 15:24:29.049866	t	f	PENDING
7	1	2025-04-24 16:33:37.531886	1	2025-04-24 16:33:37.531886	t	f	DONE
8	1	2025-04-24 16:33:37.531886	1	2025-04-24 16:33:37.531886	t	f	INITIATED
10	1	2025-04-25 16:33:37.531886	1	2025-04-25 16:33:37.531886	t	f	DONE_AND_CLOSED
11	1	2025-04-25 16:33:37.531886	1	2025-04-25 16:33:37.531886	t	f	FILLED
12	1	2025-04-25 16:33:37.531886	1	2025-04-25 16:33:37.531886	t	f	UNFILLED
9	1	2025-04-25 16:33:37.531886	1	2025-04-25 16:33:37.531886	t	f	CANCEL
\.


--
-- Data for Name: mt_users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mt_users (id, created_by, created_date, last_modified_by, last_modified_date, aadhar_card_number, first_name, is_active, is_delete, last_name, mobile_number, password, photo_path, username, role_id) FROM stdin;
43	32	2025-04-22 14:34:47.08227	32	2025-04-22 14:34:47.08227	555555556789	Pooja	t	f	Wagh	909874545	$2a$10$pKaP5Ef9KZ2.7Gzlc1QGM.0cudHn0I3TiAnjYL49ojOHCMmD7n0ZK	\N	pooja@123456	3
44	32	2025-04-22 14:37:29.035938	32	2025-04-22 14:37:29.035938	966506315812	md	t	f	dm	9665063158	$2a$10$e4XMz4GcW37iPJkhL2aXZ.cLCzMmlY7JvsOIIxm7Ers8nVxcaqj1y	\N	dj	8
56	41	2025-05-15 14:20:06.608396	41	2025-05-15 14:20:06.608396	785698745123	Aamol	t	f	Yadav	8569854789	$2a$10$5gH6QU4fMLnglJP4nmbkH.06egXaZhb6v3jjC33xcpek4fIW5MGCa	\N	aamol	8
35	2	2025-04-21 15:40:34.779707	41	2025-04-25 16:01:17.08075	123456789019	shreyash	t	f	patil	1234567899	$2a$10$ouuqiHwGTbpwKLKPC2x9kuIM./DbXUkAFCVPswWXOKSAMTj5ZwPCq	\N	shreyash1	1
38	27	2025-04-21 16:06:19.10893	45	2025-04-29 11:23:35.96131	111112222234	raju	f	f	sutar	9098767843	$2a$10$I.BW5Nbh9pzWuNHDipMu8eztwHumsZI75MBxOBWpqbMEncETlgzQS	\N	raju	8
32	2	2025-04-21 15:37:31.30483	41	2025-04-22 17:53:26.656113	032165498712	shreyash	f	f	shreyash	0321654987	$2a$10$aqbdnD3o6c6cRrzk477iSek.ZH3OhRkN12pCOg2vbbWzEWe4PdoPi	\N	shreyash	8
27	1	2025-04-21 12:55:07.280135	1	2025-04-21 15:10:56.577722	764820659627	maanik	t	f	tambolkar	1234567891	$2a$10$ZtEXZtjX5hzjpdxORAIpdu327sPPw/tfPtE47Vhn5ArCqIKni9yI2	\N	maanik	1
46	1	2025-04-28 18:50:19.292106	1	2025-04-28 18:50:19.292106	5656565678997	Sham	t	f	Jadhav	9876500000	$2a$10$xo2WoS7Fu8WflLNc.iusuuSnwbTwcj1dhCcyKXI2nv8Al4/0MK7wG	\N	sham@gmail.com	8
47	1	2025-04-28 18:57:21.402267	1	2025-04-28 18:57:21.402267	5656565678111	Ram	t	f	Jadhav	9876500001	$2a$10$M6t6TxiFgeMiMZmICkxlI./gsZwuH.Abn.AReDJlEgT/eHJu9uH9.	\N	Ram@gmail.com	8
48	1	2025-04-28 19:04:23.196075	1	2025-04-28 19:04:23.196075	5656565672222	Prince	t	f	Desuza	9876500222	$2a$10$cmPQAnvCW/RdI4iL0tPsj.r0MvfKNf8PBMm7l3R6YBtqvpBstog7W	\N	prince@gmail.com	3
6	1	2025-04-19 17:38:04.964143	41	2025-04-29 15:38:08.712065	764820659623	vinayak	f	t	deshmukh	9307084154	$2a$10$aoH4aTKkevoYBFJf2l3DWuSn7w84P9xyyMu28FKgYDmRfZ0fCGr9W	\N	vinayak	3
49	1	2025-04-30 16:59:23.472511	41	2025-05-05 18:07:24.790851	111111111111	ADMIN	f	f	ADMIN	1111111111	$2a$10$D2yf5Bkqyth7dvS10eT.NOVpLAEWj9BsYhSPPpvQlG50ZlDSiRWy2	\N	ADMIN	1
36	2	2025-04-21 15:41:00.773604	2	2025-04-21 15:41:00.773604	987654321098	raj	t	t	singh	9876543210	$2a$10$z.J/.cab0M1Z8KuAFgIaVe2KLEMs.IL8R0bF0u27EV7NxHFLTmBCq	\N	raj_singh	3
57	41	2025-05-17 15:06:25.236158	41	2025-05-17 15:06:25.236158	124323674312	Shubham	t	f	Vetal	9987965427	$2a$10$lEacVnDZLa5Dve7mkn1xa.fqiyWex1WsnYzbXyC40n6YMPfcAGXwK	\N	shubham	3
45	41	2025-04-28 18:29:08.228743	41	2025-05-17 15:12:46.132732	123045678985	Aarav	t	f	Sharma	8956321425	$2a$10$frFexKROIZLf81e35ZZ.cuBRRPta4kWEgtBpREcnKKiykP4iyJra2	\N	manager	3
59	41	2025-05-21 15:57:25.962422	41	2025-05-21 15:57:25.962422	987654321012	Ravi	t	f	Kumar	9876543211	$2a$10$R6olAcy.9/31EYbC6RR.Z.fZnjNgmOwp6g1wyTeF5Jm8NsHHk9Iwi	\N	ravi	8
60	41	2025-05-30 10:58:00.430345	41	2025-05-30 10:58:00.430345	989898760000	Ram	t	f	pawar	9518924676	$2a$10$CMVZT9T6OIbzSNX./6Dige5VMefjJ0H0XN3l2BZRcd3Y18ZfRSFzy	\N	ram	8
4	2	2025-04-19 17:29:08.829343	2	2025-04-21 15:24:26.979825	123456789512	demo	t	t	users	1234567890	$2a$10$NjE/GUhekGm0NeIj9r9WdOthlI38tGl9QkuDWzzUcrZnbMiy4Ydpm	\N	jane.smith@gmail.com	1
37	2	2025-04-21 15:41:21.51233	2	2025-04-21 15:41:21.51233	456789123456	neha	t	t	kumar	8765432109	$2a$10$EZRuaXQqmHx.F24aIuijUOIG/Qs0EEUQo/ttCqO4bKu0gh4jIou1.	\N	neha.k	3
41	2	2025-04-22 12:24:50.146945	2	2025-04-22 12:24:50.146945	123456759825	veer	t	f	veer	4567891230	$2a$10$ojkaOm0j5nZhdZwR1L9oFONL9oqJ.QOrAOOj7OdCjmvKvdEnca6jO	\N	veer	1
42	2	2025-04-22 14:30:29.306306	2	2025-04-22 14:30:29.306306	456987456321	shreyash	t	f	borkar	9146282497	$2a$10$cYvztaapRrzf8Bw1QYLm4uS09K26u5FLCJLPIr3UjQVKvbjYSEvam	\N	shreyastagram	1
39	27	2025-04-21 16:21:36.228063	41	2025-04-23 12:01:38.050886	767676767676	admin	t	t	admin	6666666666	$2a$10$kV/M1oiioeTlKkEXQqp5eO3FglxNya5Ztw1yyID6kylyYQSXvjwIi	\N	admin	1
\.


--
-- Data for Name: new_connection; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.new_connection (id, created_by, created_date, last_modified_by, last_modified_date, cash_amount, is_cash, is_dbc, is_delete, is_inventory_buy, is_new_connection, is_online, online_amount, online_photo_path, bank_account_id, customer_id) FROM stdin;
2	45	2025-05-20 12:29:22.343657	45	2025-05-20 12:29:22.343657	100	t	t	f	f	f	f	0	\N	\N	1
3	45	2025-05-20 12:30:07.637632	45	2025-05-20 12:30:07.637632	1000	t	f	f	f	t	f	0	\N	\N	2
4	45	2025-05-20 12:31:38.311444	45	2025-05-20 12:31:38.311444	400	t	t	f	f	f	f	0	\N	\N	3
5	45	2025-05-20 12:31:58.701907	45	2025-05-20 12:31:58.701907	2000	t	f	f	f	t	f	0	\N	\N	6
6	45	2025-05-20 12:32:33.823851	45	2025-05-20 12:32:33.823851	3000	t	f	f	f	t	f	0	\N	\N	5
7	45	2025-05-20 15:34:21.29693	45	2025-05-20 15:34:21.29693	1000	t	t	f	f	f	f	0	\N	\N	5
8	45	2025-05-20 15:35:41.944313	45	2025-05-20 15:35:41.944313	2000	t	t	f	f	f	f	0	\N	\N	6
9	45	2025-05-20 16:15:59.343613	45	2025-05-20 16:15:59.343613	1000	t	f	f	f	t	f	0	\N	\N	4
10	45	2025-05-22 16:36:01.190498	45	2025-05-22 16:36:01.190498	1000	t	t	f	f	f	f	0	\N	\N	1
11	45	2025-05-23 16:01:32.937388	45	2025-05-23 16:01:32.937388	1400	t	f	f	f	t	f	0	\N	\N	7
1	45	2025-05-20 12:23:02.412683	45	2025-05-20 12:23:02.412683	1000	t	f	f	f	t	f	0	\N	\N	1
12	45	2025-05-31 11:31:09.728902	45	2025-05-31 11:31:09.728902	0	t	f	f	f	t	f	0	\N	\N	8
13	45	2025-06-21 20:38:23.517831	45	2025-06-21 20:38:23.517831	300	t	f	f	f	t	f	0	\N	\N	10
14	45	2025-06-21 20:48:30.305334	45	2025-06-21 20:48:30.305334	1400	t	f	f	f	t	f	0	\N	\N	11
15	45	2025-07-22 14:57:48.58733	45	2025-07-22 14:57:48.58733	0	t	t	f	f	f	f	0	\N	\N	1
16	45	2025-07-22 14:57:49.712649	45	2025-07-22 14:57:49.712649	0	t	t	f	f	f	f	0	\N	\N	1
17	45	2025-08-19 22:47:09.083764	45	2025-08-19 22:47:09.083764	1300	t	f	f	f	t	f	0	\N	\N	12
18	45	2025-08-19 22:56:10.457982	45	2025-08-19 22:56:10.457982	200	t	t	f	f	f	f	0	\N	\N	12
\.


--
-- Data for Name: vehicle; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vehicle (id, created_by, created_date, last_modified_by, last_modified_date, fuel_type, last_service_date, load_capacity, next_service_due_date, vehicle_model, vehicle_number, vehicle_type, users_id, is_added, is_removed, is_service_done_on_due) FROM stdin;
5	1	2025-05-05 10:47:00.307747	1	2025-05-05 10:47:00.308448	DIESEL	2025-04-01	110	2025-07-01	Tata Ace HT	MH 12 AB 1234	Truck	38	t	f	f
2	45	2025-05-02 18:33:51.516281	45	2025-05-02 18:33:51.516281	DIESEL	2025-06-18	7500	2025-09-18	Tata 407	MH 14 AB 0021	Truck	32	t	f	f
7	45	2025-05-06 18:27:23.875241	45	2025-05-06 18:27:23.875241	PETROL	2025-04-15	75	2025-07-15	Mahindra Supro	MH 14 CD 5679	Mini Van	38	t	f	f
10	45	2025-06-03 11:21:54.50667	45	2025-06-03 11:21:54.50667	CNG	2025-06-03	5	2025-09-03	Tata ACE	MH12AB1234	Mini Truck	44	t	f	\N
11	41	2025-06-10 12:18:39.783492	41	2025-06-10 12:18:39.783492	PETROL	2025-06-10	400	2025-09-10	ijkl	ABCDefg	efgh	59	t	f	\N
15	45	2025-06-10 12:28:00.599156	45	2025-06-10 12:28:00.599156	DIESEL	2025-06-10	6000	2025-09-10	Mahindra Veero	MH 14 AB 0018	Mini Truck	60	t	f	\N
16	45	2025-06-10 12:32:19.524101	45	2025-06-10 12:32:19.524101	DIESEL	2025-06-10	6000	2025-09-10	Mahindra Veero	MH 14 AB 0019	Mini Truck	60	t	f	\N
17	45	2025-06-12 12:19:40.370641	45	2025-06-12 12:19:40.370641	CNG	2025-06-12	7000	2025-09-12	Mahindra Veero	MH 14 AB 0015	Mini Truck	32	t	f	\N
8	1	2025-05-06 18:35:24.614951	1	2025-05-06 18:35:24.614951	PETROL	2025-04-15	150	2025-07-15	Mahindra Jeeto	MH 14 ZZ 4567	Mini Truck	49	t	f	\N
18	45	2025-06-12 12:20:23.02475	45	2025-06-12 12:20:23.02475	CNG	2025-06-12	7000	2025-09-12	Mahindra Veero	MH 14 AB 0017	Mini Truck	47	t	f	\N
4	2	2025-05-02 19:27:24.598941	2	2025-05-02 19:27:24.598941	PETROL	2025-04-10	950	2025-07-10	Mahindra Bolero	KA 05 MN 5678	Van	38	t	f	t
3	1	2025-05-02 18:51:31.300531	1	2025-05-02 18:51:31.300531	DIESEL	2025-04-01	1000.5	2025-07-01	Tata Ace	MH 12 AB 1234	Truck	56	f	t	t
9	45	2025-06-03 10:40:12.183661	45	2025-06-03 10:40:12.183661	PETROL	2025-06-03	1000	2025-09-03	Mahindra Veero	MH 14 AB 0019	Mini Truck	38	t	f	\N
\.


--
-- Data for Name: vehicle_service_record; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vehicle_service_record (id, vehicle_service_desc, due_date_at_time_of_service, is_serviced_on_due_date, serviced_location, odo_meter_reading, vehicle_service_cost, vehicle_service_date, vehicle_serviced_by, vehicle_id) FROM stdin;
1	Routine oil change and inspection	2025-05-10	f	Pune	30000	120	2025-05-05	AutoCare Center	4
2	Brake pad replacement and tire rotation	2025-11-01	f	Pune	27000	350	2025-03-20	SpeedFix Garage	5
3	Routine oil change and inspection	2026-02-01	f	Pune	30000	120	2025-05-05	AutoCare Center	5
4	Brake system check and replacement	2025-07-15	f	Mumbai	35000	200	2025-05-10	Speedy Auto Service	7
5	Brake system check and replacement	2025-07-15	f	Pune	35000	200	2025-05-10	Speedy Auto Service	8
6	Service done	2025-09-03	f	Pune	50000	300	2025-06-03	Auto Care Car Center	9
7	Routine oil change and inspection	2025-07-10	f	Pune	20000	200	2025-06-18	OM CAR CARE CENTER	4
\.


--
-- Name: daily_assignment_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.daily_assignment_details_id_seq', 64, true);


--
-- Name: daily_assignment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.daily_assignment_id_seq', 48, true);


--
-- Name: daily_delivery_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.daily_delivery_id_seq', 60, true);


--
-- Name: delivery_exchange_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.delivery_exchange_record_id_seq', 1, false);


--
-- Name: delivery_person_closer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.delivery_person_closer_id_seq', 23, true);


--
-- Name: delivery_person_daily_closer_confirmation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.delivery_person_daily_closer_confirmation_id_seq', 22, true);


--
-- Name: end_of_day_confirmation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.end_of_day_confirmation_id_seq', 28, true);


--
-- Name: inventory_stocks_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.inventory_stocks_id_seq', 9, true);


--
-- Name: live_inventory_stocks_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.live_inventory_stocks_id_seq', 9, true);


--
-- Name: mt_agency_points_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_agency_points_id_seq', 7, true);


--
-- Name: mt_bank_accounts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_bank_accounts_id_seq', 5, true);


--
-- Name: mt_customers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_customers_id_seq', 12, true);


--
-- Name: mt_new_connections_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_new_connections_details_id_seq', 21, true);


--
-- Name: mt_product_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_product_category_id_seq', 3, true);


--
-- Name: mt_products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_products_id_seq', 9, true);


--
-- Name: mt_roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_roles_id_seq', 8, true);


--
-- Name: mt_service_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_service_types_id_seq', 2, true);


--
-- Name: mt_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_status_id_seq', 7, true);


--
-- Name: mt_users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mt_users_id_seq', 60, true);


--
-- Name: new_connection_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.new_connection_id_seq', 18, true);


--
-- Name: vehicle_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.vehicle_id_seq', 18, true);


--
-- Name: vehicle_service_record_id_seq1; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.vehicle_service_record_id_seq1', 10, true);


--
-- Name: daily_assignment_details daily_assignment_details_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment_details
    ADD CONSTRAINT daily_assignment_details_pkey PRIMARY KEY (id);


--
-- Name: daily_assignment daily_assignment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment
    ADD CONSTRAINT daily_assignment_pkey PRIMARY KEY (id);


--
-- Name: daily_delivery daily_delivery_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_delivery
    ADD CONSTRAINT daily_delivery_pkey PRIMARY KEY (id);


--
-- Name: delivery_exchange_record delivery_exchange_record_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_exchange_record
    ADD CONSTRAINT delivery_exchange_record_pkey PRIMARY KEY (id);


--
-- Name: delivery_person_closer delivery_person_closer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_person_closer
    ADD CONSTRAINT delivery_person_closer_pkey PRIMARY KEY (id);


--
-- Name: delivery_person_daily_closer_confirmation delivery_person_daily_closer_confirmation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_person_daily_closer_confirmation
    ADD CONSTRAINT delivery_person_daily_closer_confirmation_pkey PRIMARY KEY (id);


--
-- Name: end_of_day_confirmation end_of_day_confirmation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.end_of_day_confirmation
    ADD CONSTRAINT end_of_day_confirmation_pkey PRIMARY KEY (id);


--
-- Name: inventory_stocks inventory_stocks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory_stocks
    ADD CONSTRAINT inventory_stocks_pkey PRIMARY KEY (id);


--
-- Name: live_inventory_stocks live_inventory_stocks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.live_inventory_stocks
    ADD CONSTRAINT live_inventory_stocks_pkey PRIMARY KEY (id);


--
-- Name: mt_agency_points mt_agency_points_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_agency_points
    ADD CONSTRAINT mt_agency_points_pkey PRIMARY KEY (id);


--
-- Name: mt_bank_accounts mt_bank_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_bank_accounts
    ADD CONSTRAINT mt_bank_accounts_pkey PRIMARY KEY (id);


--
-- Name: mt_customers mt_customers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_customers
    ADD CONSTRAINT mt_customers_pkey PRIMARY KEY (id);


--
-- Name: mt_new_connections_details mt_new_connections_details_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_new_connections_details
    ADD CONSTRAINT mt_new_connections_details_pkey PRIMARY KEY (id);


--
-- Name: mt_product_category mt_product_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_product_category
    ADD CONSTRAINT mt_product_category_pkey PRIMARY KEY (id);


--
-- Name: mt_products mt_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_products
    ADD CONSTRAINT mt_products_pkey PRIMARY KEY (id);


--
-- Name: mt_roles mt_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_roles
    ADD CONSTRAINT mt_roles_pkey PRIMARY KEY (id);


--
-- Name: mt_service_types mt_service_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_service_types
    ADD CONSTRAINT mt_service_types_pkey PRIMARY KEY (id);


--
-- Name: mt_status mt_status_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_status
    ADD CONSTRAINT mt_status_pkey PRIMARY KEY (id);


--
-- Name: mt_users mt_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_users
    ADD CONSTRAINT mt_users_pkey PRIMARY KEY (id);


--
-- Name: new_connection new_connection_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.new_connection
    ADD CONSTRAINT new_connection_pkey PRIMARY KEY (id);


--
-- Name: mt_customers uk4v8os9l5mhkoks5el7vj6y17m; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_customers
    ADD CONSTRAINT uk4v8os9l5mhkoks5el7vj6y17m UNIQUE (mobile_number);


--
-- Name: mt_users ukbgu1x1e6vqk96g3bva9wi2j69; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_users
    ADD CONSTRAINT ukbgu1x1e6vqk96g3bva9wi2j69 UNIQUE (aadhar_card_number);


--
-- Name: mt_users ukpumv1b8cp6w71pyomk6hkjdn8; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_users
    ADD CONSTRAINT ukpumv1b8cp6w71pyomk6hkjdn8 UNIQUE (username);


--
-- Name: mt_roles ukq9hk16dm4nvekwxh59phikwp9; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_roles
    ADD CONSTRAINT ukq9hk16dm4nvekwxh59phikwp9 UNIQUE (role);


--
-- Name: mt_users ukquroq0wwodcp14ohoewq52ros; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_users
    ADD CONSTRAINT ukquroq0wwodcp14ohoewq52ros UNIQUE (mobile_number);


--
-- Name: vehicle vehicle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle
    ADD CONSTRAINT vehicle_pkey PRIMARY KEY (id);


--
-- Name: vehicle_service_record vehicle_service_record_pkey1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle_service_record
    ADD CONSTRAINT vehicle_service_record_pkey1 PRIMARY KEY (id);


--
-- Name: daily_assignment fk2c8drijn9x5x55fc47f77dvqx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment
    ADD CONSTRAINT fk2c8drijn9x5x55fc47f77dvqx FOREIGN KEY (assigned_by_id) REFERENCES public.mt_users(id);


--
-- Name: daily_delivery fk3s5crrkbbgjd4f9yykbkh9mjo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_delivery
    ADD CONSTRAINT fk3s5crrkbbgjd4f9yykbkh9mjo FOREIGN KEY (point_id) REFERENCES public.mt_agency_points(id);


--
-- Name: mt_new_connections_details fk4d7c5e3u29qv2mxeyymqxvw4e; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_new_connections_details
    ADD CONSTRAINT fk4d7c5e3u29qv2mxeyymqxvw4e FOREIGN KEY (product_id) REFERENCES public.mt_products(id);


--
-- Name: new_connection fk4ik3unrfacxcgtexhe6p7gta1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.new_connection
    ADD CONSTRAINT fk4ik3unrfacxcgtexhe6p7gta1 FOREIGN KEY (customer_id) REFERENCES public.mt_customers(id);


--
-- Name: daily_assignment fk4t9xgr4nx2qcvptpuv926t0ai; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment
    ADD CONSTRAINT fk4t9xgr4nx2qcvptpuv926t0ai FOREIGN KEY (customer_id) REFERENCES public.mt_customers(id);


--
-- Name: new_connection fk5h843iejh8scnuafnmt71wjcl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.new_connection
    ADD CONSTRAINT fk5h843iejh8scnuafnmt71wjcl FOREIGN KEY (bank_account_id) REFERENCES public.mt_bank_accounts(id);


--
-- Name: daily_assignment fk5pdudamcpcuj9q6j4p9ywqxfo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment
    ADD CONSTRAINT fk5pdudamcpcuj9q6j4p9ywqxfo FOREIGN KEY (status_id) REFERENCES public.mt_status(id);


--
-- Name: delivery_exchange_record fk69bc4keviucbinrrh6nufbioo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_exchange_record
    ADD CONSTRAINT fk69bc4keviucbinrrh6nufbioo FOREIGN KEY (product_id) REFERENCES public.mt_products(id);


--
-- Name: daily_delivery fk7htx6api5v9541pexensi002y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_delivery
    ADD CONSTRAINT fk7htx6api5v9541pexensi002y FOREIGN KEY (delivery_person_id) REFERENCES public.mt_users(id);


--
-- Name: mt_new_connections_details fk8ui3ybohilgrhqpoq0r52h4fq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_new_connections_details
    ADD CONSTRAINT fk8ui3ybohilgrhqpoq0r52h4fq FOREIGN KEY (new_connection_id) REFERENCES public.new_connection(id);


--
-- Name: daily_delivery fk9n7b7fibc5swgbl99tx6w15o5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_delivery
    ADD CONSTRAINT fk9n7b7fibc5swgbl99tx6w15o5 FOREIGN KEY (bank_account_id) REFERENCES public.mt_bank_accounts(id);


--
-- Name: inventory_stocks fk9xke4gooskmcde4avjj7i773t; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory_stocks
    ADD CONSTRAINT fk9xke4gooskmcde4avjj7i773t FOREIGN KEY (product_category_id) REFERENCES public.mt_product_category(id);


--
-- Name: live_inventory_stocks fkayuer3hf3py0nf0c6y8qsu11c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.live_inventory_stocks
    ADD CONSTRAINT fkayuer3hf3py0nf0c6y8qsu11c FOREIGN KEY (product_category_id) REFERENCES public.mt_product_category(id);


--
-- Name: end_of_day_confirmation fkb17l0773p2wq9chyvwi8plx8n; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.end_of_day_confirmation
    ADD CONSTRAINT fkb17l0773p2wq9chyvwi8plx8n FOREIGN KEY (status_id) REFERENCES public.mt_status(id);


--
-- Name: mt_users fkbodx0gbvrdwflo556xjbmuyt8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_users
    ADD CONSTRAINT fkbodx0gbvrdwflo556xjbmuyt8 FOREIGN KEY (role_id) REFERENCES public.mt_roles(id);


--
-- Name: mt_products fkd2xhhfado9b1482c576j52395; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mt_products
    ADD CONSTRAINT fkd2xhhfado9b1482c576j52395 FOREIGN KEY (product_category_id) REFERENCES public.mt_product_category(id);


--
-- Name: daily_assignment fkd387gus1g5an53crk32s3n8tg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment
    ADD CONSTRAINT fkd387gus1g5an53crk32s3n8tg FOREIGN KEY (delivery_person_id) REFERENCES public.mt_users(id);


--
-- Name: inventory_stocks fkda2b82n8olb2k4bqxli1y18uj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory_stocks
    ADD CONSTRAINT fkda2b82n8olb2k4bqxli1y18uj FOREIGN KEY (product_id) REFERENCES public.mt_products(id);


--
-- Name: daily_delivery fkda6ntuhc99ewj0d9h2u4a0vtj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_delivery
    ADD CONSTRAINT fkda6ntuhc99ewj0d9h2u4a0vtj FOREIGN KEY (product_id) REFERENCES public.mt_products(id);


--
-- Name: vehicle_service_record fkebvwm85ejto5v19laehx52mn7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle_service_record
    ADD CONSTRAINT fkebvwm85ejto5v19laehx52mn7 FOREIGN KEY (vehicle_id) REFERENCES public.vehicle(id);


--
-- Name: daily_assignment fkekm4qqks6t37vc52xdbudgo50; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment
    ADD CONSTRAINT fkekm4qqks6t37vc52xdbudgo50 FOREIGN KEY (agency_point_id) REFERENCES public.mt_agency_points(id);


--
-- Name: delivery_person_daily_closer_confirmation fkf2wfa6ku05dulg4stgrjbnbom; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_person_daily_closer_confirmation
    ADD CONSTRAINT fkf2wfa6ku05dulg4stgrjbnbom FOREIGN KEY (status_id) REFERENCES public.mt_status(id);


--
-- Name: live_inventory_stocks fkhe8yks7yxm6el5g0ev0e2pucw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.live_inventory_stocks
    ADD CONSTRAINT fkhe8yks7yxm6el5g0ev0e2pucw FOREIGN KEY (product_id) REFERENCES public.mt_products(id);


--
-- Name: daily_assignment_details fkhgxaircfyj4fe0q6drnkrgydl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment_details
    ADD CONSTRAINT fkhgxaircfyj4fe0q6drnkrgydl FOREIGN KEY (product_id) REFERENCES public.mt_products(id);


--
-- Name: end_of_day_confirmation fkhieniryu6jbmyj3xqjol8rs6h; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.end_of_day_confirmation
    ADD CONSTRAINT fkhieniryu6jbmyj3xqjol8rs6h FOREIGN KEY (confirmed_by_id) REFERENCES public.mt_users(id);


--
-- Name: delivery_exchange_record fki5p7oa76k7fnruvkcta9pclbq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_exchange_record
    ADD CONSTRAINT fki5p7oa76k7fnruvkcta9pclbq FOREIGN KEY (product_category_id) REFERENCES public.mt_product_category(id);


--
-- Name: delivery_exchange_record fkkp96gtmgdgnj7xkw0rh7tjrv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_exchange_record
    ADD CONSTRAINT fkkp96gtmgdgnj7xkw0rh7tjrv FOREIGN KEY (delivery_boy_id) REFERENCES public.mt_users(id);


--
-- Name: vehicle fklmpvq381gnii4bsamtmlebb58; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle
    ADD CONSTRAINT fklmpvq381gnii4bsamtmlebb58 FOREIGN KEY (users_id) REFERENCES public.mt_users(id);


--
-- Name: delivery_person_daily_closer_confirmation fkn4skrgdm33o9gmxj63l6k3yex; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_person_daily_closer_confirmation
    ADD CONSTRAINT fkn4skrgdm33o9gmxj63l6k3yex FOREIGN KEY (confirmed_by_id) REFERENCES public.mt_users(id);


--
-- Name: end_of_day_confirmation fknh869q8jqjly81wu8oecn3vrp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.end_of_day_confirmation
    ADD CONSTRAINT fknh869q8jqjly81wu8oecn3vrp FOREIGN KEY (send_by_id) REFERENCES public.mt_users(id);


--
-- Name: daily_delivery fko6hys13ewo2ej35xd9serb7mb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_delivery
    ADD CONSTRAINT fko6hys13ewo2ej35xd9serb7mb FOREIGN KEY (status_id) REFERENCES public.mt_status(id);


--
-- Name: daily_delivery fkon4trudrsqtc3h2p8jua39pr6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_delivery
    ADD CONSTRAINT fkon4trudrsqtc3h2p8jua39pr6 FOREIGN KEY (customer_id) REFERENCES public.mt_customers(id);


--
-- Name: delivery_person_closer fkor5ewqo1mqdayfv1aofdd7ux9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_person_closer
    ADD CONSTRAINT fkor5ewqo1mqdayfv1aofdd7ux9 FOREIGN KEY (daily_assignment_id) REFERENCES public.daily_assignment(id);


--
-- Name: daily_assignment_details fkpala56t9xwbj1oebomsdrgbgn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment_details
    ADD CONSTRAINT fkpala56t9xwbj1oebomsdrgbgn FOREIGN KEY (product_category_id) REFERENCES public.mt_product_category(id);


--
-- Name: delivery_person_daily_closer_confirmation fkpyrc3jd21mls7prrd720nah2o; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.delivery_person_daily_closer_confirmation
    ADD CONSTRAINT fkpyrc3jd21mls7prrd720nah2o FOREIGN KEY (daily_person_closer_id) REFERENCES public.daily_assignment(id);


--
-- Name: daily_assignment_details fkqn5okoun69dkbbls0x639fwt4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_assignment_details
    ADD CONSTRAINT fkqn5okoun69dkbbls0x639fwt4 FOREIGN KEY (daily_assignment) REFERENCES public.daily_assignment(id);


--
-- Name: daily_delivery fkror4a4hh7eli5enrlvmjoj7o6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daily_delivery
    ADD CONSTRAINT fkror4a4hh7eli5enrlvmjoj7o6 FOREIGN KEY (daily_assignment_id) REFERENCES public.daily_assignment(id);


--
-- PostgreSQL database dump complete
--

