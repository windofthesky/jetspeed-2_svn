
-----------------------------------------------------------------------------
-- pref_property_set_def
-----------------------------------------------------------------------------
CREATE TABLE PREF_PROPERTY_SET_DEF
(
    property_set_def_id INTEGER NOT NULL PRIMARY KEY,
    property_set_name VARCHAR(100),
    property_set_type SMALLINT,
    creation_date TIMESTAMP,
    modified_date TIMESTAMP
);

-----------------------------------------------------------------------------
-- pref_node
-----------------------------------------------------------------------------
CREATE TABLE PREF_NODE
(
    node_id INTEGER NOT NULL PRIMARY KEY,
    parent_node_id INTEGER NULL,
    property_set_def_id INTEGER NULL,
    node_name VARCHAR(100),
    node_type SMALLINT,
    full_path VARCHAR(254),
    creation_date TIMESTAMP,
    modified_date TIMESTAMP
);


-----------------------------------------------------------------------------
-- pref_property_value
-----------------------------------------------------------------------------
CREATE TABLE PREF_PROPERTY_VALUE
(
    property_value_id INTEGER NOT NULL PRIMARY KEY,
    property_key_id INTEGER,
    node_id INTEGER,
    boolean_value BIT,
    datetime_value TIMESTAMP,
    long_value INTEGER,
    double_value DOUBLE,
    text_value VARCHAR(254),
    creation_date TIMESTAMP,
    modified_date TIMESTAMP
);

-----------------------------------------------------------------------------
-- pref_property_key
-----------------------------------------------------------------------------
CREATE TABLE PREF_PROPERTY_KEY
(
    property_key_id INTEGER NOT NULL PRIMARY KEY,
    property_set_def_id INTEGER,
    property_name VARCHAR(100),
    property_type SMALLINT,
    creation_date TIMESTAMP,
    modified_date TIMESTAMP
);

    ALTER TABLE PREF_NODE
        ADD CONSTRAINT PREF_NODE_FK1 FOREIGN KEY (parent_node_id)
            REFERENCES PREF_NODE (node_id);

    ALTER TABLE PREF_NODE
        ADD CONSTRAINT PREF_NODE_FK_2 FOREIGN KEY (property_set_def_id)
            REFERENCES PREF_PROPERTY_SET_DEF (property_set_def_id);

    ALTER TABLE PREF_PROPERTY_VALUE
        ADD CONSTRAINT PREF_PROPERTY_VALUE_FK_1 FOREIGN KEY (node_id)
            REFERENCES PREF_NODE (node_id);
            
    ALTER TABLE PREF_PROPERTY_VALUE
        ADD CONSTRAINT PREF_PROPERTY_VALUE_FK_2 FOREIGN KEY (property_key_id)
            REFERENCES PREF_PROPERTY_KEY (property_key_id);

    ALTER TABLE PREF_PROPERTY_KEY
        ADD CONSTRAINT PREF_PROPERTY_KEY_FK_1 FOREIGN KEY (property_set_def_id)
            REFERENCES PREF_PROPERTY_SET_DEF (property_set_def_id);


