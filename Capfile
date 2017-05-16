# Load DSL and set up stages
require "capistrano/setup"

# Include default deployment tasks
require "capistrano/deploy"

# Load the SCM plugin
require "capistrano/scm/git"
install_plugin Capistrano::SCM::Git

# ssh into boxes for deploy (normally done with dlss-capistrano, but that does a bundle-audit)
require 'capistrano/one_time_key'

# update shared_configs with deploy (normally done with dlss-capistrano, but that does a bundle-audit)
require 'capistrano/shared_configs'

# Load custom tasks from `lib/capistrano/tasks` if you have any defined
# Dir.glob("lib/capistrano/tasks/*.rake").each { |r| import r }
