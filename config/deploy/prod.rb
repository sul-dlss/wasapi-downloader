server 'was-downloader.stanford.edu', user: 'was', roles: 'app'

set :bundle_without, 'deployment'

# allow ssh to host
Capistrano::OneTimeKey.generate_one_time_key!
