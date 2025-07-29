<svelte:options runes={true} />
<script lang="ts">
	import { session, type Session } from '$components/session/session.store';
	import { page } from '$app/stores';
	import Intercom, { update } from '@intercom/messenger-js-sdk';
	import { fromStore } from 'svelte/store';

	const { children } = $props();

	let initialized = false;
	const sess = fromStore(session);
	const pg = fromStore(page);

	$effect(() => {
		if (sess.current.loggedIn !== 'loading' && sess.current.loggedIn && 
			sess.current.user !== 'loading' && 
			sess.current.userProfile !== 'loading') {
			
			const props = {
				app_id: 'wbln6mnh',
				user_id: sess.current.user.uuid,
				email: sess.current.userProfile.email,
				name: sess.current.userProfile.email,
				created_at: new Date(sess.current.userProfile.createdAt).getTime() / 1000,
			};
			if (sess.current.userProfile.firstName) {
				props.name = sess.current.userProfile.firstName;
			}
			if (sess.current.userProfile.lastName) {
				props.name += ' ' + sess.current.userProfile.lastName;
			}
			if (initialized){
				update(props);
			} else {
				console.log('Initializing Intercom with', props);
				Intercom(props);
				initialized = true;
			}
		}
	});
	$effect(() => {
		// @ts-ignore - The update function does not need to be called with any arguments https://developers.intercom.com/installing-intercom/web/methods#intercomupdate
		if (pg.current.route) update();
	});
</script>
{@render children()}