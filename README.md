# lein-gen-src

 gen-src is a Leiningen plugin that generates source and test template files (clojure.test). The plugin assumes that you want the generated files in the namespace of the project. 
The plugin only works in the context of a leiningen project.
There is no provision to re-define the top namespace qualifier. i.e. This is an exercise project more than anything else.


For convenience, you might want to add the plugin to the user profile in ~/.lein/profiles.clj instead of directly in project.clj. 

The plugin is not on clojars. If you want to install it localy, you can follow [this] (http://david-demainlalune.github.io/blog/2013/08/07/clojure-private-repositories-and-lein-plugins/) procedure.


## Usage

Once installed the plugin gives access to the "gen-src" task.

running the following command in a project named fun
    
    $ lein gen-src foo.bar

will generate two files (with minimal ns declarations)

    src/fun/foo/bar.clj
    test/fun/foo/bar_test.clj



## License

Copyright © 2013 david hodgetts

Distributed under the MIT License. Use at your own risk
