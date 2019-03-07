(function () {

	var plugin = new (Java.extend(Plugin, KeyboardInputPlugin, {
		init: function (model) {

		},
		onKeyPressed: function (model, key, shift) {
			if (key == "z".charCodeAt(0)) {
				model.setLevel(model.createLevel("menu"));
				View.update(model);
			}
		},
		getName: function () { return "ReturnPlugin" },
	}))();

	return plugin;
}());