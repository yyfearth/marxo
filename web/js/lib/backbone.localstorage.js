/**
 * Backbone localStorage Adapter
 * Version 1.1.4
 *
 * https://github.com/jeromegn/Backbone.localStorage
 *
 * Modified by Wilson Young for auto create LocalStorage for Collection and Model by URL
 *
 */

define('lib/backbone.localstorage', ['lib/common'], function () {

// Generate four random hex digits.
	function S4() {
		return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
	}

// Generate a pseudo-object-id by concatenating random hexadecimal.
	function oid() {
		return S4() + S4() + S4() + S4() + S4() + S4();
	}

// Our Store is represented by a single JS object in *localStorage*. Create it
// with a meaningful name, like the name you'd give a table.
	Backbone.LocalStorage = window.Store = function (name) {
		if (!this.localStorage) {
			throw 'Backbone.localStorage: Environment does not support localStorage.'
		}
		this.name = name;
		var store = this.localStorage().getItem(this.name);
		this.records = (store && store.split(',')) || [];
	};

	var separator = '/'

	_.extend(Backbone.LocalStorage.prototype, {

		// Save the current state of the **Store** to *localStorage*.
		save: function () {
			this.localStorage().setItem(this.name, this.records.join(','));
		},

		// Add a model, giving it a (hopefully)-unique OID, if it doesn't already
		// have an id of it's own.
		create: function (model) {
			if (!model.id) {
				model.id = oid();
				model.set(model.idAttribute, model.id);
			}
			model.set({created_at: new Date(), updated_at: new Date()});
			this.localStorage().setItem(this.name + separator + model.id, JSON.stringify(model));
			this.records.push(model.id.toString());
			this.save();
			return this.find(model);
		},

		// Update a model by replacing its copy in `this.data`.
		update: function (model) {
			this.localStorage().setItem(this.name + separator + model.id, JSON.stringify(model));
			if (!_.include(this.records, model.id.toString()))
				this.records.push(model.id.toString());
			model.set('updated_at', new Date());
			this.save();
			return this.find(model);
		},

		// Retrieve a model from `this.data` by id.
		find: function (model) {
			return this.jsonData(this.localStorage().getItem(this.name + separator + model.id));
		},

		// Return the array of all models currently in storage.
		findAll: function () {
			// Lodash removed _#chain in v1.0.0-rc.1
			return (_.chain || _)(this.records)
				.map(function (id) {
					return this.jsonData(this.localStorage().getItem(this.name + separator + id));
				}, this)
				.compact()
				.value();
		},

		// Delete a model from `this.data`, returning it.
		destroy: function (model) {
			if (model.isNew())
				return false;
			this.localStorage().removeItem(this.name + separator + model.id);
			this.records = _.reject(this.records, function (id) {
				return id === model.id.toString();
			});
			this.save();
			return model;
		},

		localStorage: function () {
			return localStorage;
		},

		// fix for 'illegal access' error on Android when JSON.parse is passed null
		jsonData: function (data) {
			return data && JSON.parse(data);
		},

		// Clear localStorage for specific collection.
		_clear: function () {
			var local = this.localStorage(),
				itemRe = new RegExp('^' + this.name + separator);

			// Remove id-tracking item (e.g., 'foo').
			local.removeItem(this.name);

			// Lodash removed _#chain in v1.0.0-rc.1
			// Match all data items (e.g., 'foo-ID') and remove.
			(_.chain || _)(local).keys()
				.filter(function (k) {
					return itemRe.test(k);
				})
				.each(function (k) {
					local.removeItem(k);
				});
		},

		// Size of localStorage.
		_storageSize: function () {
			return this.localStorage().length;
		}

	});

// localSync delegate to the model or collection's
// *localStorage* property, which should be an instance of `Store`.
// window.Store.sync and Backbone.localSync is deprecated, use Backbone.LocalStorage.sync instead
	var stores = {};
	Backbone.LocalStorage.sync = window.Store.sync = Backbone.localSync = function (method, model, options) {
		var key;
		var url = typeof model.url == 'function' ? model.url() : model.url || '';
		if (model instanceof Backbone.Model) {
			key = model.urlRoot || url.replace(model.id, '');
			if (key.slice(-1) === '/')
				key = key.slice(0, -1);
		} else if (model instanceof Backbone.Collection) {
			key = url;
		}
		var store = stores[key];
		if (store == null)
			store = stores[key] = new Backbone.LocalStorage(key);

		var resp, errorMessage, syncDfd = Backbone.$.Deferred && Backbone.$.Deferred(); //If $ is having Deferred - use it.

		try {
			console.log('Sync Req:', method, url, model.toJSON());
			switch (method) {
				case 'read':
					resp = model.id != undefined ? store.find(model) : store.findAll();
					break;
				case 'create':
					resp = store.create(model);
					break;
				case 'update':
					resp = store.update(model);
					break;
				case 'delete':
					resp = store.destroy(model);
					break;
			}

		} catch (error) {
			if (error.code === DOMException.QUOTA_EXCEEDED_ERR && store._storageSize() === 0)
				errorMessage = 'Private browsing is unsupported';
			else
				errorMessage = error.message;
		}

		if (resp) {
			if (options && options.success) {
				options.success(resp);
			}
			console.log('Sync Resp:', method, url);
			console[model.length ? 'table' : 'dir'](model.toJSON());
			if (syncDfd) {
				syncDfd.resolve(resp);
			}
		} else {
			errorMessage = errorMessage ? errorMessage
				: 'Record Not Found';
			console.error('Error:', errorMessage);
			if (options && options.error)
				options.error(errorMessage);

			if (syncDfd)
				syncDfd.reject(errorMessage);
		}

		// add compatibility with $.ajax
		// always execute callback for success and error
		if (options && options.complete) options.complete(resp);

		return syncDfd && syncDfd.promise();
	};

	Backbone.ajaxSync = Backbone.sync;

	Backbone.sync = Backbone.LocalStorage.sync;

	return Backbone.LocalStorage;
});
