# lein-gen-src

A Leiningen plugin to generate source files with their corresponding test files (clojure.test)


## Usage


Put `[lein-gen-src "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj.


running the following command in a project named fun
    $ lein gen-src foo.bar

will generate two files (with minimal ns declarations)

    src/fun/foo/bar.clj
    test/fun/foo/bar_test.clj

### Warning !!

it does not (yet) verify the eventual presence of existing files. It will overwrite no questions asked.

## License

Copyright © 2013 david hodgetts

Distributed under the MIT License.
