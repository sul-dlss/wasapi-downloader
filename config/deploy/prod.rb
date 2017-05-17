# prod specific deployment info goes here
set :deploy_host, "was-downloader.stanford.edu"
server fetch(:deploy_host), user: fetch(:user), roles: 'app'

set :bundle_without, 'deployment'

# allow ssh to host
Capistrano::OneTimeKey.generate_one_time_key!
