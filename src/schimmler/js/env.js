// Some generic definitions loaded globally into the script engine responsible for all plugins for smaller and handier scripts

// load the mozilla utility script to provide the global "importPackage" function
load("nashorn:mozilla_compat.js");

// load all classes of the architecture sub-package into the global scope
importPackage("schimmler.architecture")
importPackage("schimmler.architecture.plugin")

// load the java.lang defaults into the global scope
importPackage("java.lang")