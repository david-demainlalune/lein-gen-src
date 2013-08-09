
require 'fileutils'



INTERNAL_REPOS_PATH = File.join('i:', 'clojure', 'internal_repos')

RAKE_WORKING_DIR = Dir.pwd

JAR_NAME = 'lein-gen-src-0.1.0-SNAPSHOT.jar'

JAR_PATH = File.join(RAKE_WORKING_DIR, 'target', JAR_NAME)
POM_PATH = File.join(RAKE_WORKING_DIR, "pom.xml")

def add_pretty_line(message)
	puts
	puts '-' * 40
	puts message
	puts '-' * 40
	puts
end


def delete_files_in_path(path)
	Dir.foreach(path) do |file|

		full_file_path = File.join(path, file)

		puts "file #{file} full_file_path #{full_file_path}"

		if file != '.' && file != '..'

			if File.directory?(full_file_path)
				puts "recur dir #{full_file_path}"
				delete_files_in_path(full_file_path)
				FileUtils.rmdir(full_file_path, {:verbose => true })
			else
				puts "kill file #{full_file_path}"
				FileUtils.rm full_file_path
			end
		end
	end
	
end


def copy_asset(source_dir, destination_dir)
	FileUtils.cp_r(File.join(source_dir, '.'), destination_dir, :verbose => true)
end



###############################################
# tasks

desc "build all "
task :build => [:lein_install, :copy_to_internal_repos] do
	add_pretty_line('build finished')
end



desc "runs lein install"
task :lein_install do

	add_pretty_line('running lein install')

	success = `lein install`

	puts success
end


desc "copy *.jar and pom.xml to internal_repos"
task :copy_to_internal_repos do
	add_pretty_line("copying #{JAR_NAME} and pom.xml to internal_repos")
	FileUtils.cp(JAR_PATH, INTERNAL_REPOS_PATH, :verbose => true)
	FileUtils.cp(POM_PATH, INTERNAL_REPOS_PATH, :verbose => true)
end
