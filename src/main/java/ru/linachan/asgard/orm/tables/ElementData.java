/**
 * This class is generated by jOOQ
 */
package ru.linachan.asgard.orm.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.5" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ElementData extends org.jooq.impl.TableImpl<ru.linachan.asgard.orm.tables.records.ElementDataRecord> {

	private static final long serialVersionUID = 1300406623;

	/**
	 * The singleton instance of <code>asgard.element_data</code>
	 */
	public static final ru.linachan.asgard.orm.tables.ElementData ELEMENT_DATA = new ru.linachan.asgard.orm.tables.ElementData();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<ru.linachan.asgard.orm.tables.records.ElementDataRecord> getRecordType() {
		return ru.linachan.asgard.orm.tables.records.ElementDataRecord.class;
	}

	/**
	 * The column <code>asgard.element_data.id</code>.
	 */
	public final org.jooq.TableField<ru.linachan.asgard.orm.tables.records.ElementDataRecord, java.lang.Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>asgard.element_data.name</code>.
	 */
	public final org.jooq.TableField<ru.linachan.asgard.orm.tables.records.ElementDataRecord, java.lang.String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

	/**
	 * The column <code>asgard.element_data.repository_url</code>.
	 */
	public final org.jooq.TableField<ru.linachan.asgard.orm.tables.records.ElementDataRecord, java.lang.String> REPOSITORY_URL = createField("repository_url", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

	/**
	 * The column <code>asgard.element_data.element_path</code>.
	 */
	public final org.jooq.TableField<ru.linachan.asgard.orm.tables.records.ElementDataRecord, java.lang.String> ELEMENT_PATH = createField("element_path", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

	/**
	 * The column <code>asgard.element_data.last_update</code>.
	 */
	public final org.jooq.TableField<ru.linachan.asgard.orm.tables.records.ElementDataRecord, java.lang.Integer> LAST_UPDATE = createField("last_update", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * Create a <code>asgard.element_data</code> table reference
	 */
	public ElementData() {
		this("element_data", null);
	}

	/**
	 * Create an aliased <code>asgard.element_data</code> table reference
	 */
	public ElementData(java.lang.String alias) {
		this(alias, ru.linachan.asgard.orm.tables.ElementData.ELEMENT_DATA);
	}

	private ElementData(java.lang.String alias, org.jooq.Table<ru.linachan.asgard.orm.tables.records.ElementDataRecord> aliased) {
		this(alias, aliased, null);
	}

	private ElementData(java.lang.String alias, org.jooq.Table<ru.linachan.asgard.orm.tables.records.ElementDataRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, ru.linachan.asgard.orm.Asgard.ASGARD, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<ru.linachan.asgard.orm.tables.records.ElementDataRecord, java.lang.Integer> getIdentity() {
		return ru.linachan.asgard.orm.Keys.IDENTITY_ELEMENT_DATA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<ru.linachan.asgard.orm.tables.records.ElementDataRecord> getPrimaryKey() {
		return ru.linachan.asgard.orm.Keys.KEY_ELEMENT_DATA_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<ru.linachan.asgard.orm.tables.records.ElementDataRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<ru.linachan.asgard.orm.tables.records.ElementDataRecord>>asList(ru.linachan.asgard.orm.Keys.KEY_ELEMENT_DATA_PRIMARY, ru.linachan.asgard.orm.Keys.KEY_ELEMENT_DATA_UNIQUE_NAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ru.linachan.asgard.orm.tables.ElementData as(java.lang.String alias) {
		return new ru.linachan.asgard.orm.tables.ElementData(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ru.linachan.asgard.orm.tables.ElementData rename(java.lang.String name) {
		return new ru.linachan.asgard.orm.tables.ElementData(name, null);
	}
}
