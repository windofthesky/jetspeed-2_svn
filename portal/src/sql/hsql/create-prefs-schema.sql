
-----------------------------------------------------------------------------
-- pref_property_set_def
-----------------------------------------------------------------------------
CREATE TABLE pref_property_set_def
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
CREATE TABLE pref_node
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
CREATE TABLE pref_property_value
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
CREATE TABLE pref_property_key
(
    property_key_id INTEGER NOT NULL PRIMARY KEY,
    property_set_def_id INTEGER,
    property_name VARCHAR(100),
    property_type SMALLINT,
    creation_date TIMESTAMP,
    modified_date TIMESTAMP
);

	ALTER TABLE pref_node
		ADD CONSTRAINT pref_node_FK1 FOREIGN KEY (parent_node_id)
			REFERENCES pref_node (node_id);

    ALTER TABLE pref_node
        ADD CONSTRAINT pref_node_FK_2 FOREIGN KEY (property_set_def_id)
            REFERENCES pref_property_set_def (property_set_def_id);

	ALTER TABLE pref_property_value
        ADD CONSTRAINT pref_property_value_FK_1 FOREIGN KEY (node_id)
            REFERENCES pref_node (node_id);
            
    ALTER TABLE pref_property_value
        ADD CONSTRAINT pref_property_value_FK_2 FOREIGN KEY (property_key_id)
            REFERENCES pref_property_key (property_key_id);

    ALTER TABLE pref_property_key
        ADD CONSTRAINT pref_property_key_FK_1 FOREIGN KEY (property_set_def_id)
            REFERENCES pref_property_set_def (property_set_def_id);


