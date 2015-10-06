/**
 * This class is generated by jOOQ
 */
package ru.linachan.asgard.orm.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.5" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ElementDataRecord extends org.jooq.impl.UpdatableRecordImpl<ru.linachan.asgard.orm.tables.records.ElementDataRecord> implements org.jooq.Record5<java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer> {

	private static final long serialVersionUID = 921552034;

	/**
	 * Setter for <code>asgard.element_data.id</code>.
	 */
	public void setId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>asgard.element_data.id</code>.
	 */
	public java.lang.Integer getId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>asgard.element_data.name</code>.
	 */
	public void setName(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>asgard.element_data.name</code>.
	 */
	public java.lang.String getName() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>asgard.element_data.repository_url</code>.
	 */
	public void setRepositoryUrl(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>asgard.element_data.repository_url</code>.
	 */
	public java.lang.String getRepositoryUrl() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>asgard.element_data.element_path</code>.
	 */
	public void setElementPath(java.lang.String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>asgard.element_data.element_path</code>.
	 */
	public java.lang.String getElementPath() {
		return (java.lang.String) getValue(3);
	}

	/**
	 * Setter for <code>asgard.element_data.last_update</code>.
	 */
	public void setLastUpdate(java.lang.Integer value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>asgard.element_data.last_update</code>.
	 */
	public java.lang.Integer getLastUpdate() {
		return (java.lang.Integer) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Integer> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer> fieldsRow() {
		return (org.jooq.Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer> valuesRow() {
		return (org.jooq.Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return ru.linachan.asgard.orm.tables.ElementData.ELEMENT_DATA.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return ru.linachan.asgard.orm.tables.ElementData.ELEMENT_DATA.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return ru.linachan.asgard.orm.tables.ElementData.ELEMENT_DATA.REPOSITORY_URL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field4() {
		return ru.linachan.asgard.orm.tables.ElementData.ELEMENT_DATA.ELEMENT_PATH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field5() {
		return ru.linachan.asgard.orm.tables.ElementData.ELEMENT_DATA.LAST_UPDATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getRepositoryUrl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value4() {
		return getElementPath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value5() {
		return getLastUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElementDataRecord value1(java.lang.Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElementDataRecord value2(java.lang.String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElementDataRecord value3(java.lang.String value) {
		setRepositoryUrl(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElementDataRecord value4(java.lang.String value) {
		setElementPath(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElementDataRecord value5(java.lang.Integer value) {
		setLastUpdate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElementDataRecord values(java.lang.Integer value1, java.lang.String value2, java.lang.String value3, java.lang.String value4, java.lang.Integer value5) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ElementDataRecord
	 */
	public ElementDataRecord() {
		super(ru.linachan.asgard.orm.tables.ElementData.ELEMENT_DATA);
	}

	/**
	 * Create a detached, initialised ElementDataRecord
	 */
	public ElementDataRecord(java.lang.Integer id, java.lang.String name, java.lang.String repositoryUrl, java.lang.String elementPath, java.lang.Integer lastUpdate) {
		super(ru.linachan.asgard.orm.tables.ElementData.ELEMENT_DATA);

		setValue(0, id);
		setValue(1, name);
		setValue(2, repositoryUrl);
		setValue(3, elementPath);
		setValue(4, lastUpdate);
	}
}
