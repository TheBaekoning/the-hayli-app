<svelte:options runes={true} />
<script lang="ts">
	import { restore, session } from './session.store';
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import { browser } from '$app/environment';
	import { fromStore } from 'svelte/store';

	const unauthedRoutes: RegExp[] = [/^\/$/, /^\/sign-up(.+)?/, /^\/login/];
	const pageStore = fromStore(page);
	const sessionStore = fromStore(session);

	function authRedir(){
		if (sessionStore.current.userProfile === "loading") return;
		if (sessionStore.current.loggedIn && unauthedRoutes.some(route => route.test(pageStore.current.url.pathname))) {
			if (browser) {
				//console.log('goToDashboard');
				goto('/health');
			}
		}
		if (!sessionStore.current.loggedIn && !unauthedRoutes.some(route => route.test(pageStore.current.url.pathname))) {
			if (browser) {
				goto('/');
			}
		}
	}

	session.subscribe(authRedir);
	page.subscribe(authRedir);
	$effect(() => {
		restore().then(() => {
			authRedir();
		});
	});
</script>
